using Newtonsoft.Json;
using SpriteSheetManagerLibraries.Interfaces;

namespace SpriteSheetManagerLibraries.Converters
{
    public class InternalConverter : ConverterBase
    {
        public override bool CanRead => true;
        public override bool CanWrite => true;
        public override string FileFilter => $"SpritesheetManager files (*.{FileExtension})|*.{FileExtension}";
        public override string FileExtension => "spm";

        /// <summary>
        /// Accepts a serialized instance of ISpriteSheet and instantiates it.
        /// </summary>
        /// <param name="spriteSheetData"></param>
        /// <returns></returns>
        public override ISpriteSheet FromString(string spriteSheetData)
        {
            var settings = new JsonSerializerSettings()
            {
                TypeNameHandling = TypeNameHandling.All
            };
            return JsonConvert.DeserializeObject<ISpriteSheet>(spriteSheetData, settings);
        }

        public override string ToString(ISpriteSheet spriteSheet)
        {
            var settings = new JsonSerializerSettings()
            {
                TypeNameHandling = TypeNameHandling.All
            };
            return JsonConvert.SerializeObject(spriteSheet, settings);
        }
    }
}