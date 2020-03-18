using Newtonsoft.Json;

namespace SvyU.Models
{
    public class SingleResponse : IResponseItem
    {
        public QuestionType Type => QuestionType.Single;

        [JsonProperty("answer")]
        public int Response { get; set; }
    }
}
