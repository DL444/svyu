using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

namespace SvyU.Models
{
    public class SingleQuestion : ChoiceQuestion
    {
        public override QuestionType Type => QuestionType.Single;
    }
}
