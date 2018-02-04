using System.IO;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public abstract class ConverterBase : IConvertSpriteSheets
    {
        public abstract bool CanRead { get; }
        public abstract bool CanWrite { get; }
        public abstract ISpriteSheet FromString(string spriteSheetData);

        public abstract string ToString(ISpriteSheet spriteSheet);

        public virtual ISpriteSheet ReadSpriteSheet(string fullPath)
        {
            var sheet= FromString(File.ReadAllText(fullPath));
            SetBaseDir(sheet, fullPath);
            return sheet;
        }

        private void SetBaseDir(ISpriteSheet sheet, string fullPath)
        {
            sheet.BaseDir = Path.GetDirectoryName(fullPath);
        }

        public virtual string GetFileName(ISpriteSheet spriteSheet) => $"{Path.Combine(spriteSheet.BaseDir, Path.GetFileNameWithoutExtension(spriteSheet.ImageFileName))}.spm.json";

        public virtual void SaveSpriteSheet(ISpriteSheet spriteSheet)
        {
            File.WriteAllText(GetFileName(spriteSheet), ToString(spriteSheet));
        }
    }
}