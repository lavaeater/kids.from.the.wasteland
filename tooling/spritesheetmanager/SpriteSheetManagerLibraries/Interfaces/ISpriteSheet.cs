using System.Collections.Generic;
using System.Windows;

namespace SpriteSheetManager.Interfaces
{
    public interface ISpriteSheet
    {
        string ImageFileName { get; set; }
        string BaseDir { get; set; }
        string Format { get; set; }
        Size Size { get; set; }
        string Application { get; set; }
        string Version { get; set; }
        List<ISpriteSheetFrame> Frames { get; set; }
    }
}