using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace SvyU.Models
{
    [JsonObject(
        NamingStrategyType = typeof(CamelCaseNamingStrategy),
        NamingStrategyParameters = new object[] { false, false })]
    internal class SurveySerializationRoot
    {
        public SurveySerializationRoot(Survey survey) => Survey = survey;
        public Survey Survey { get; set; }
    }
}
