using System;
using Newtonsoft.Json;
using SvyU.Models;
using Xunit;

namespace SvyU.Test
{
    public class SerializationTest
    {
        [Fact]
        public void TestSerialization()
        {
            string expected = "{\"survey\":{\"id\":\"12344134\",\"len\":\"3\",\"questions\":[{\"type\":\"single\"," +
                "\"question\":\"How well do the professors teach at this university?\",\"options\":[{\"1\":\"Extremely well\"}," +
                "{\"2\":\"Very well\"}]},{\"type\":\"multiple\",\"question\":\"How effective is the teaching outside yur major at the univesrity?\"," +
                "\"options\":[{\"1\":\"Extremetly effective\"},{\"2\":\"Very effective\"},{\"3\":\"Somewhat effective\"},{\"4\":\"Not so effective\"}," +
                "{\"5\":\"Not at all effective\"}]},{\"type\":\"text\",\"question\":\"Some question\"}]}}";

            IQuestion[] questions = new IQuestion[3];
            questions[0] = new SingleQuestion()
            {
                Question = "How well do the professors teach at this university?",
                Options = new[]
                {
                    "Extremely well",
                    "Very well"
                }
            };
            questions[1] = new MultipleQuestion()
            {
                Question = "How effective is the teaching outside yur major at the univesrity?",
                Options = new[]
                {
                    "Extremetly effective",
                    "Very effective",
                    "Somewhat effective",
                    "Not so effective",
                    "Not at all effective"
                }
            };
            questions[2] = new TextQuestion()
            {
                Question = "Some question"
            };

            Survey survey = new Survey()
            {
                Id = "12344134",
                Questions = questions
            };

            Assert.Equal(expected, survey.GetJson());
        }
    }
}
