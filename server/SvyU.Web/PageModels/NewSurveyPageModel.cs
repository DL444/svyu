using System.Collections.Generic;
using System.Linq;
using SvyU.Models;

namespace SvyU.Web.PageModels
{
    internal class NewSurveyPageModel
    {
        public List<IQuestionPageModel> Questions { get; } = new List<IQuestionPageModel>();

        public Survey GetSurveyModel() => new Survey()
        {
            Questions = Questions.Select(x => x.GetQuestionModel()).ToArray()
        };
    }
}
