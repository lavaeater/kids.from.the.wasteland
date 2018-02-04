using System.Windows;

namespace SpriteSheetManager.Models
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