using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

namespace SvyU.Models
{
    [Serializable]
    internal struct OptionSerializationObject : ISerializable
    {
        public OptionSerializationObject(int index, string value)
        {
            Index = index;
            Value = value;
        }

        public int Index { get; set; }
        public string Value { get; set; }

        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue(Index.ToString(), Value);
        }
    }
}
