using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.Extensions.Logging;

namespace SvyU.Backend
{
    public static class ResponseFunction
    {
        [FunctionName("response")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "post", Route = "response")] HttpRequest req,
            [CosmosDB(databaseName: "Survey", collectionName: "Survey", ConnectionStringSetting = "DbConnection")]
                IAsyncCollector<ResponseEntity> entityCollector,
            ILogger log)
        {
            log.LogInformation("Response endpoint received a request. Method = {method}", req.Method);

            string json = await req.ReadAsStringAsync();
            if (string.IsNullOrWhiteSpace(json))
            {
                log.LogInformation("Client supplied empty body.");
                return new BadRequestResult();
            }

            bool success = await ResponseDatabaseWriter.WriteToDatabaseAsync(json, entityCollector, log);
            return success ? (IActionResult)new OkResult() : new BadRequestResult();
        }
    }
}
