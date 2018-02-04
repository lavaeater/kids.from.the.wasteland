using System.IO;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public abstract class ConverterBase : IConvertSpriteSheets
    {
        public abstract ISpriteSheet FromString(string spriteSheetData);

        public abstract string ToString(ISpriteSheet spriteSheet);

        public virtual ISpriteSheet ReadSpriteSheet(string fileName)
        {
            return FromString(File.ReadAllText(fileName));
        }

        public virtual string GetFileName(ISpriteSheet spriteSheet) => $"{Path.Combine(Path.GetDirectoryName(spriteSheet.ImageFileName), Path.GetFileNameWithoutExtension(spriteSheet.ImageFileName))}.spm.json";

        public virtual void SaveSpriteSheet(ISpriteSheet spriteSheet)
        {
            File.WriteAllText(GetFileName(spriteSheet), ToString(spriteSheet));
        }
    }
}