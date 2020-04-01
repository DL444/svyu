using Newtonsoft.Json;

namespace SvyU.Models
{
    public class StarRateResponse : IResponseItem
    {
        public QuestionType Type => QuestionType.StarRate;

        [JsonProperty("answer")]
        public double Response { get; set; }
    }
}
