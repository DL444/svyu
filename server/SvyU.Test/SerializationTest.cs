using SvyU.Models;
using Xunit;

namespace SvyU.Test
{
    public class SerializationTest
    {
        [Fact]
        public void TestSurveySerialization()
        {
            string expected = "{\"survey\":{\"id\":\"12344134\",\"len\":\"4\",\"questions\":[{\"type\":\"single\"," +
                "\"question\":\"How well do the professors teach at this university?\",\"options\":[{\"1\":\"Extremely well\"}," +
                "{\"2\":\"Very well\"}]},{\"type\":\"multiple\",\"question\":\"How effective is the teaching outside yur major at the univesrity?\"," +
                "\"options\":[{\"1\":\"Extremetly effective\"},{\"2\":\"Very effective\"},{\"3\":\"Somewhat effective\"},{\"4\":\"Not so effective\"}," +
                "{\"5\":\"Not at all effective\"}]},{\"type\":\"text\",\"question\":\"Some question\"},{\"type\":\"starRate\",\"question\":\"Star rating question\"}]}}";

            IQuestion[] questions = new IQuestion[4];
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
            questions[3] = new StarRateQuestion()
            {
                Question = "Star rating question"
            };

            Survey survey = new Survey()
            {
                Id = "12344134",
                Questions = questions
            };

            Assert.Equal(expected, survey.GetJson());
        }

        [Fact]
        public void TestResponseDeserialization()
        {
            string json = "{\"id\":\"1234567\",\"len\":4,\"longitude\":18.23,\"latitude\":19.25,\"time\":12345,\"imei\":\"ABC\"," +
                "\"answers\":[{\"type\":\"single\",\"answer\":1},{\"type\":\"multiple\",\"answer\":[0,1,3]},{\"type\":\"text\",\"answer\":\"something\"}," +
                "{\"type\":\"starRate\",\"answer\":3}]}";
            Response response = Response.Parse(json);

            Assert.Equal("1234567", response.Id);
            Assert.Equal(4, response.Length);
            Assert.Equal(18.23, response.Longitude);
            Assert.Equal(19.25, response.Latitude);
            Assert.Equal(12345, response.Timestamp);
            Assert.Equal("ABC", response.Imei);
            Assert.Equal(4, response.Responses.Length);

            Assert.Equal(QuestionType.Single, response.Responses[0].Type);
            Assert.IsType<SingleResponse>(response.Responses[0]);
            Assert.Equal(1, ((SingleResponse)response.Responses[0]).Response);

            Assert.Equal(QuestionType.Multiple, response.Responses[1].Type);
            Assert.IsType<MultipleResponse>(response.Responses[1]);
            Assert.Equal(new[] { 0, 1, 3 }, ((MultipleResponse)response.Responses[1]).Response);

            Assert.Equal(QuestionType.Text, response.Responses[2].Type);
            Assert.IsType<TextResponse>(response.Responses[2]);
            Assert.Equal("something", ((TextResponse)response.Responses[2]).Response);

            Assert.Equal(QuestionType.StarRate, response.Responses[3].Type);
            Assert.IsType<StarRateResponse>(response.Responses[3]);
            Assert.Equal(3, ((StarRateResponse)response.Responses[3]).Response);
        }
    }
}
