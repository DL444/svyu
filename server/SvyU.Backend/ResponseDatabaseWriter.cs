using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.WebJobs;
using SvyU.Models;
using Microsoft.Extensions.Logging;

namespace SvyU.Backend
{
    internal static class ResponseDatabaseWriter
    {
        public static async Task<bool> WriteToDatabaseAsync(string json, IAsyncCollector<ResponseEntity> entityCollector, ILogger log)
        {
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
                return false;
            }

            log.LogInformation("Creating new entity.");
            ResponseEntity entity = new ResponseEntity()
            {
                Response = response
            };
            await entityCollector.AddAsync(entity);
            log.LogInformation("New entity created. ID = {responseId}", entity.DocumentId);
            return true;
        }
    }
}
