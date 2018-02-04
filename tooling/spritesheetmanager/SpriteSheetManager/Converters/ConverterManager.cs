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
        public Dictionary<string, IConvertSpriteSheets> Converters { get; set; }

        public ConverterService()
        {
            Converters = new Dictionary<string, IConvertSpriteSheets>();
            SetupConverters();
        }

        private void SetupConverters()
        {
            Converters.Add("internal", new InternalConverter());
            Converters.Add("pixijs", new PixiJsConverter());
        }
    }


}
