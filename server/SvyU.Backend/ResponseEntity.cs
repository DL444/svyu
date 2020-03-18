using System;
using Newtonsoft.Json;
using SvyU.Models;

namespace SvyU.Backend
{
    public class ResponseEntity
    {
        public Response Response { get; set; }

        public Guid ResponseId { get; set; } = Guid.NewGuid();

        public string PartitionKey => Response.Id;

        [JsonProperty("id")]
        public string DocumentId => $"response-{PartitionKey}-{ResponseId}";
    }
}
