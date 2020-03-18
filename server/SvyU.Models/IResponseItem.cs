using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace SvyU.Models
{
    public interface IResponseItem
    {
        [JsonConverter(typeof(StringEnumConverter))]
        QuestionType Type { get; }
    }
}
