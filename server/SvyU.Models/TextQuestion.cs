using System;
using System.Collections.Generic;
using System.Text;

namespace SvyU.Models
{
    public class TextQuestion : IQuestion
    {
        public QuestionType Type => QuestionType.Text;
        public string Question { get; set; }
    }
}
