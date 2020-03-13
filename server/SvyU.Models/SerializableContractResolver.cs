using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using Newtonsoft.Json.Serialization;

namespace SvyU.Models
{
    internal class SerializableContractResolver : DefaultContractResolver
    {
        protected override JsonContract CreateContract(Type objectType)
        {
            if (typeof(ISerializable).IsAssignableFrom(objectType))
            {
                return CreateISerializableContract(objectType);
            }
            return base.CreateContract(objectType);
        }
    }
}
