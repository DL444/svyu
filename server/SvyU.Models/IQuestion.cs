using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Serialization;

namespace SvyU.Models
{
    [JsonObject(
        NamingStrategyType = typeof(CamelCaseNamingStrategy),
        NamingStrategyParameters = new object[] { false, false })]
    public interface IQuestion
    {
        string Question { get; set; }

        [JsonConverter(typeof(StringEnumConverter),
            converterParameters: new object[] { typeof(CamelCaseNamingStrategy) })]
        QuestionType Type { get; }
    }
}
