using SpriteSheetManager.Models;

namespace SpriteSheetManager.Interfaces
{
    public interface IConvertSpriteSheets
    {
        bool CanRead { get; }
        bool CanWrite { get; }
        ISpriteSheet FromString(string spriteSheetData);
        string ToString(ISpriteSheet spriteSheet);
        /// <summary>
        /// Reads a file of sprite sheet data and returns an instance of ISpriteSheet
        /// </summary>
        /// <param name="fullPath">The full path to the data we wish to read</param>
        /// <returns></returns>
        ISpriteSheet ReadSpriteSheet(string fullPath);
        string GetFileName(ISpriteSheet spriteSheet);
        void SaveSpriteSheet(ISpriteSheet spriteSheet);
    }
}
