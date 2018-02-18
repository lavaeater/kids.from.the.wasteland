using System;
using System.IO;
using System.Linq;
using SpriteSheetManager.Converters;

namespace SpriteSheetConverter
{
    class Program
    {
        static void Main(string[] args)
        {
            if(!args.Any()) throw new ApplicationException("You need to provide a base path for the sheets");
            string basePath = args[0];
            if (!Directory.Exists(basePath)) throw new DirectoryNotFoundException("The supplied path does not exist");
            var converterService = new ConverterService();
            foreach (var tileDir in Directory.GetDirectories(basePath))
            {
                var tilesetDir = Path.GetFileName(tileDir);
                var tilePath = Path.Combine(tileDir, $"{tilesetDir}.spm");
                if (File.Exists(tilePath))
                {
                    var sheet = converterService.Converters[ConverterService.Internal].ReadSpriteSheet(tilePath);
                    converterService.Converters[ConverterService.TexturePacker].SaveSpriteSheet(sheet);
                }
            }
        }
    }
}
