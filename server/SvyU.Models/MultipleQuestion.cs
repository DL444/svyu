using System;
using System.Collections.Generic;
using System.Text;

namespace SvyU.Models
{
    public class MultipleQuestion : ChoiceQuestion
    {
        public override QuestionType Type => QuestionType.Multiple;
    }
}
