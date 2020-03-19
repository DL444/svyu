using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.Extensions.Logging;

namespace SvyU.Backend
{
    public static class SurveyFunction
    {
        [FunctionName("survey")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "get", "post", Route = "survey/{id}")] HttpRequest req,
            [CosmosDB(databaseName: "Survey", collectionName: "Survey", ConnectionStringSetting = "DbConnection",
                PartitionKey = "{id}", Id = "survey-{id}")] SurveyEntity entity,
            [CosmosDB(databaseName: "Survey", collectionName: "Survey", ConnectionStringSetting = "DbConnection")]
                IAsyncCollector<SurveyEntity> entityCollector,
            string id, ILogger log)
        {
            log.LogInformation("Survey endpoint received a request. Method = {method}, Id = {id}", req.Method, id);
            if (id == null)
            {
                log.LogInformation("Client specified null ID.");
                return new BadRequestResult();
            }

            if (req.Method == HttpMethods.Get)
            {
                if (entity == null)
                {
                    log.LogInformation("The an entry with specified ID was not found.");
                    return new NotFoundResult();
                }
                else
                {
                    log.LogInformation("Entry found.");
                    return new OkObjectResult(entity.JsonPayload);
                }
            }
            else if (req.Method == HttpMethods.Post)
            {
                if (entity != null)
                {
                    log.LogInformation("An entry with specified ID already exists.");
                    return new ConflictResult();
                }
                else
                {
                    log.LogInformation("Creating new entity.");
                    string jsonPayload = await req.ReadAsStringAsync();
                    var newEntity = new SurveyEntity()
                    {
                        SurveyId = id,
                        JsonPayload = jsonPayload
                    };
                    await entityCollector.AddAsync(newEntity);
                    log.LogInformation("New entity created.");
                    return new OkResult();
                }
            }
            else
            {
                log.LogInformation("The client sent a request with invalid method.");
                return new BadRequestResult();
            }
        }
    }
}
