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

    public static class RectExtensions
    {

        public static Int32Rect ToInt32Rect(this Rect inputRect)
        {
            var rect = new Int32Rect((int)inputRect.X, (int)inputRect.Y, (int)inputRect.Width, (int)inputRect.Height);
            return rect;
        }
    }
}