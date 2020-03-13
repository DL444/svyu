using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;

namespace SvyU.Models
{
    public static class SurveySerializationExtensions
    {
        public static string GetJson(this Survey survey)
        {
            SurveySerializationRoot root = new SurveySerializationRoot(survey);
            return JsonConvert.SerializeObject(root, new JsonSerializerSettings()
            {
                ContractResolver = resolver
            });
        }

        private static SerializableContractResolver resolver = new SerializableContractResolver();
    }
}
