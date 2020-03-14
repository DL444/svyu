using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SvyU.Models;

namespace SvyU.Web.PageModels
{
    internal interface IQuestionPageModel
    {
        string Question { get; set; }
        QuestionType Type { get; }
        bool InEdit { get; set; }

        IQuestion GetQuestionModel();
    }

    internal class OptionPageModel
    {
        public string Option { get; set; }
    }

    internal abstract class ChoiceQuestionPageModel : IQuestionPageModel
    {
        public string Question { get; set; }
        public abstract QuestionType Type { get; }
        public List<OptionPageModel> Options { get; } = new List<OptionPageModel>();
        public bool InEdit { get; set; }
        public OptionPageModel OptionPlaceholder { get; set; } = new OptionPageModel();

        public abstract IQuestion GetQuestionModel();
    }

    internal class SingleQuestionPageModel : ChoiceQuestionPageModel
    {
        public override QuestionType Type => QuestionType.Single;
        public override IQuestion GetQuestionModel() => new SingleQuestion()
        {
            Question = Question,
            Options = Options.Select(x => x.Option).Where(x => !string.IsNullOrWhiteSpace(x)).ToArray()
        };
    }

    internal class MultipleQuestionPageModel : ChoiceQuestionPageModel
    {
        public override QuestionType Type => QuestionType.Multiple;
        public override IQuestion GetQuestionModel() => new MultipleQuestion()
        {
            Question = Question,
            Options = Options.Select(x => x.Option).Where(x => !string.IsNullOrWhiteSpace(x)).ToArray()
        };
    }

    internal class TextQuestionPageModel : IQuestionPageModel
    {
        public string Question { get; set; }
        public QuestionType Type => QuestionType.Text;
        public bool InEdit { get; set; }

        public IQuestion GetQuestionModel() => new TextQuestion()
        {
            Question = Question
        };
    }
}
