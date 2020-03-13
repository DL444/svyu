using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;

namespace SvyU.Backend
{
    public class SurveyEntity
    {
        public string SurveyId { get; set; }

        public string JsonPayload { get; set; }

        public string PartitionKey => SurveyId;

        [JsonProperty("id")]
        public string Id => $"survey-{PartitionKey}";
    }
}
