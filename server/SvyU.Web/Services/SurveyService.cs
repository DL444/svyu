using System;
using System.Net.Http;
using System.Threading.Tasks;
using SvyU.Models;

namespace SvyU.Web.Services
{
    internal class SurveyService
    {
        public SurveyService(HttpClient client, Random random)
        {
            this.client = client;
            this.random = random;
        }

        public async Task<int> AddSurvey(Survey survey)
        {
            int id = 0;
            bool conflict = false;
            do
            {
                id = random.Next(0, int.MaxValue);
                survey.Id = id.ToString();
                string json = survey.GetJson();
                HttpResponseMessage response = await client.PostAsync($"https://svyu.azure-api.net/survey/{id}", new StringContent(json));
                conflict = response.StatusCode == System.Net.HttpStatusCode.Conflict;
            }
            while (conflict);
            return id;
        }

        private readonly HttpClient client;
        private readonly Random random;
    }
}
