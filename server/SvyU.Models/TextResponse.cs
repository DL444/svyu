using Newtonsoft.Json;

namespace SvyU.Models
{
    public class TextResponse : IResponseItem
    {
        public QuestionType Type => QuestionType.Text;

        [JsonProperty("answer")]
        public string Response { get; set; }
    }
}
