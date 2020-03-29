using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using System.Collections.Generic;

namespace SvyU.Backend
{
    public static class BatchResponseFunction
    {
        [FunctionName("batchResponse")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "post", Route = "batchResponse")] HttpRequest req,
            [CosmosDB(databaseName: "Survey", collectionName: "Survey", ConnectionStringSetting = "DbConnection")]
                IAsyncCollector<ResponseEntity> entityCollector,
            ILogger log)
        {
            log.LogInformation("Batch response endpoint received a request. Method = {method}", req.Method);

            string json = await req.ReadAsStringAsync();
            if (string.IsNullOrWhiteSpace(json))
            {
                log.LogInformation("Client supplied empty body.");
                return new BadRequestResult();
            }

            log.LogInformation("Parsing batch array.");
            JArray array;
            try
            {
                array = JArray.Parse(json);
            }
            catch (Exception ex)
            {
                log.LogWarning("Exception occured parsing response JSON.");
                log.LogWarning("Message: {exception}", ex.Message);
                log.LogWarning("Stacktrace: \n{stacktrace}", ex.StackTrace);
                log.LogWarning("JSON: \n{responseJson}", json);
                log.LogInformation("Assuming client sent invalid JSON.");
                return new BadRequestResult();
            }
            log.LogInformation("Successfully parsed batch array.");

            List<int> failIndex = new List<int>();
            for (int i = 0; i < array.Count; i++)
            {
                string itemJson = array[i].ToString();
                bool success = await ResponseDatabaseWriter.WriteToDatabaseAsync(itemJson, entityCollector, log);
                if (!success)
                {
                    log.LogWarning($"Writing item at index {i} failed.");
                    failIndex.Add(i);
                }
            }

            if (failIndex.Count == 0)
            {
                return new OkResult();
            }
            else
            {
                return new BadRequestObjectResult(new { failedItems = failIndex });
            }
        }
    }
}
