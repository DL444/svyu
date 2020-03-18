using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using SvyU.Models;

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

            Response response;
            try
            {
                response = Response.Parse(json);
                log.LogInformation("Successfully parsed incoming JSON.");
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

            log.LogInformation("Creating new entity.");
            ResponseEntity entity = new ResponseEntity()
            {
                Response = response
            };
            await entityCollector.AddAsync(entity);
            log.LogInformation("New entity created.");
            return new OkResult();
        }
    }
}
