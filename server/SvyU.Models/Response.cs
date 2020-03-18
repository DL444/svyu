using Newtonsoft.Json;

namespace SvyU.Models
{

    public class Response
    {
        [JsonRequired]
        public string Id { get; set; }

        [JsonProperty("len")]
        public int Length => Responses.Length;

        public double Longitude { get; set; }

        public double Latitude { get; set; }

        [JsonRequired]
        [JsonProperty("time")]
        public long Timestamp { get; set; }

        public string Imei { get; set; }

        [JsonRequired]
        [JsonProperty("answers")]
        public IResponseItem[] Responses { get; set; }

        public static Response Parse(string json)
        {
            return JsonConvert.DeserializeObject<Response>(json, new ResponseItemConverter());
        }
    }
}
