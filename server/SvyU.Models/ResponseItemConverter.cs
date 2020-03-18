using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace SvyU.Models
{
    internal class ResponseItemConverter : JsonConverter
    {
        // Do not use Type.IsAssignableFrom(). Will cause recursion.
        public override bool CanConvert(Type objectType) => objectType == typeof(IResponseItem);

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            JObject obj = JObject.Load(reader);
            string type = obj["type"].Value<string>();
            switch (type.ToUpperInvariant())
            {
                case "SINGLE":
                    return obj.ToObject<SingleResponse>(serializer);
                case "MULTIPLE":
                    return obj.ToObject<MultipleResponse>(serializer);
                case "TEXT":
                    return obj.ToObject<TextResponse>(serializer);
                default:
                    throw new NotSupportedException($"The question type {type} is not supported.");
            }
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            serializer.Serialize(writer, value);
        }
    }
}
