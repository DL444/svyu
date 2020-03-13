using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Serialization;

namespace SvyU.Models
{
    public enum QuestionType
    {
        Single = 0,
        Multiple = 1,
        Text = 2
    }
}
