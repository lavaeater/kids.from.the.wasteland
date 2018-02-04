using System.Windows;

namespace SpriteSheetManager.Interfaces
{
    public interface ISpriteSheetFrame
    {
        string Key { get; set; }
        bool Rotated { get; set; }
        Size SourceSize { get; set; }
        Rect SpriteSourceSize { get; set; }
        Rect TextureRegion { get; set; }
        bool Trimmed { get; set; }
    }
}