namespace SvyU.Models
{
    public class StarRateQuestion : IQuestion
    {
        public QuestionType Type => QuestionType.StarRate;

        public string Question { get; set; }
    }
}
