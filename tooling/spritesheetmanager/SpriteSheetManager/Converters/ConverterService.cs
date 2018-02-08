using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SpriteSheetManager.Interfaces;

namespace SpriteSheetManager.Converters
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
    }


}
