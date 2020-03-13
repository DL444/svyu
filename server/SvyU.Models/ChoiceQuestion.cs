using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

namespace SvyU.Models
{
    [Serializable]
    public abstract class ChoiceQuestion : IQuestion, ISerializable
    {
        public ChoiceQuestion() { }

        protected ChoiceQuestion(SerializationInfo serializationInfo, StreamingContext streamingContext)
        {
            throw new NotSupportedException("Deserialization of this type is not currently supported.");
        }

        public string Question { get; set; }
        public abstract QuestionType Type { get; }
        public string[] Options { get; set; } = new string[0];

        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue(nameof(Type).ToLowerInvariant(), Type.ToString().ToLowerInvariant());
            info.AddValue(nameof(Question).ToLowerInvariant(), Question);
            OptionSerializationObject[] serializationObjects = new OptionSerializationObject[Options.Length];
            for (int i = 0; i < Options.Length; i++)
            {
                serializationObjects[i] = new OptionSerializationObject(i + 1, Options[i]);
            }
            info.AddValue(nameof(Options).ToLowerInvariant(), serializationObjects);
        }
    }
}
