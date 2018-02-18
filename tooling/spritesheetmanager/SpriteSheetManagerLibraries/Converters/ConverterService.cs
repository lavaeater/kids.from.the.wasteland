using System.Collections.Generic;
using System.Linq;
using SpriteSheetManagerLibraries.Interfaces;

namespace SpriteSheetManagerLibraries.Converters
{
    public class ConverterService
    {
        public static string PixiJjs = "pixijs";
        public static string Internal = "internal";
        public Dictionary<string, IConvertSpriteSheets> Converters { get; set; }
        public static string TexturePacker = "TexturePacker";

        public ConverterService()
        {
            Converters = new Dictionary<string, IConvertSpriteSheets>();
            SetupConverters();
        }

        private void SetupConverters()
        {
            Converters.Add(Internal, new InternalConverter());
            Converters.Add(PixiJjs, new PixiJsConverter());
            Converters.Add(TexturePacker, new TexturePackerConverter());
        }

        public IConvertSpriteSheets GetConverter(string fileExtension)
        {
            return Converters.Values.FirstOrDefault(c => fileExtension.Contains(c.FileExtension));
        }

        public string LoadableExtensions => Converters.Values.Where(c => c.CanRead).Select(c => c.FileFilter).Aggregate((i, j) => i + "|" + j) + "|All files(*.*)|*.*"; 

        public string WriteableExtensions => Converters.Values.Where(c => c.CanWrite).Select(c => c.FileFilter)
                    .Aggregate((i, j) => i + "|" + j);
    }
}
