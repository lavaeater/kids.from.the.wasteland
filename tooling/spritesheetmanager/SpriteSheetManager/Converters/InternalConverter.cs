using Newtonsoft.Json;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public class InternalConverter : ConverterBase
    {
        public override bool CanRead => true;
        public override bool CanWrite => true;

        /// <summary>
        /// Accepts a serialized instance of ISpriteSheet and instantiates it.
        /// </summary>
        /// <param name="spriteSheetData"></param>
        /// <returns></returns>
        public override ISpriteSheet FromString(string spriteSheetData)
        {
            return JsonConvert.DeserializeObject<ISpriteSheet>(spriteSheetData);
        }

        public override string ToString(ISpriteSheet spriteSheet)
        {
            return JsonConvert.SerializeObject(spriteSheet);
        }
    }
}