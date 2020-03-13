using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace SvyU.Models
{
    [JsonObject(
        NamingStrategyType = typeof(CamelCaseNamingStrategy), 
        NamingStrategyParameters = new object[] { false, false })]
    public class Survey
    {
        public string Id { get; set; }

        [JsonProperty("len")]
        public string Length => Questions.Length.ToString();

        public IQuestion[] Questions { get; set; } = new IQuestion[0];
    }
}
