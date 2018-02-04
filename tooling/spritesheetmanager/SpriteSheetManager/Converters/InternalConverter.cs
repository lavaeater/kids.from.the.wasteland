using Newtonsoft.Json;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public class InternalConverter : ConverterBase
    {
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