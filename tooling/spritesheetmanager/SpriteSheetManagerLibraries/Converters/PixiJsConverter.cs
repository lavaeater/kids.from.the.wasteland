using System.Windows;
using Newtonsoft.Json.Linq;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public class PixiJsConverter : ConverterBase
    {
        public override bool CanRead => true;
        public override bool CanWrite => false;
        public override string FileFilter => $"PixiJS (*.{FileExtension})|*.{FileExtension}";
        public override string FileExtension => "json";

        public override ISpriteSheet FromString(string spriteSheetData)
        {
            dynamic jsonObject = JObject.Parse(spriteSheetData) as dynamic;
            dynamic meta = jsonObject.meta;
            var spriteSheet = new SpriteSheet
            {
                Application = meta.app,
                ImageFileName = meta.image,
                Version = meta.version,
                Format = meta.format,
                Size = new Size((double) meta.size.w, (double) meta.size.h)
            };

            var frames = jsonObject.frames as JObject;

            foreach (var pair in frames)
            {
                var key = pair.Key;
                var frame = pair.Value as dynamic;

                var spriteSheetFrame = new SpriteSheetFrame()
                {
                    Key = key,
                    Rotated = frame.rotated,
                    SourceSize = new Size((double)frame.sourceSize.w, (double)frame.sourceSize.h),
                    SpriteSourceSize = new Rect(new Point((double)frame.spriteSourceSize.x, (double)frame.spriteSourceSize.y), new Size((double)frame.spriteSourceSize.w, (double)frame.spriteSourceSize.h)),
                    TextureRegion = new Rect(new Point((double)frame.frame.x, (double)frame.frame.y), new Size((double)frame.frame.w, (double)frame.frame.h)),
                    Trimmed = frame.trimmed
                };
                spriteSheet.Frames.Add(spriteSheetFrame);
            }

            return spriteSheet;
        }

        public override string ToString(ISpriteSheet spriteSheet)
        {
            throw new System.NotImplementedException();
        }
    }
}
