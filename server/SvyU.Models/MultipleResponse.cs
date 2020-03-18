using System;
using Newtonsoft.Json;

namespace SvyU.Models
{
    public class MultipleResponse : IResponseItem
    {
        public QuestionType Type => QuestionType.Multiple;

        [JsonProperty("answer")]
        public int[] Response { get; set; }
    }
}
