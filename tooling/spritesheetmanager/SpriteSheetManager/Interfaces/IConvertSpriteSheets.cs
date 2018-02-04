using SpriteSheetManager.Models;

namespace SpriteSheetManager.Interfaces
{
    public interface IConvertSpriteSheets
    {
        ISpriteSheet FromString(string spriteSheetData);
        string ToString(ISpriteSheet spriteSheet);
    }

    public interface IReadAndWriteSpriteSheets
    {
        ISpriteSheet ReadSpriteSheet(string fileName);
        string GetFileName(ISpriteSheet spriteSheet);
        void SaveSpriteSheet(ISpriteSheet spriteSheet);
    }
}
