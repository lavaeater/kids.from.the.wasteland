using System.Collections.Generic;
using System.Windows;

namespace SpriteSheetManager.Models
{
    public interface ISpriteSheet
    {
        string ImageFileName { get; set; }
        string Format { get; set; }
        Size Size { get; set; }
        string Application { get; set; }
        string Version { get; set; }
        Dictionary<string, ISpriteSheetFrame> Frames { get; set; }
    }

    public class SpriteSheet : ISpriteSheet
    {
        public string ImageFileName { get; set; }
        public string Format { get; set; }
        public Size Size { get; set; }
        public string Application { get; set; }
        public string Version { get; set; }
        public Dictionary<string, ISpriteSheetFrame> Frames { get; set; }
    }

    public class SpriteSheetFrame : ISpriteSheetFrame
    {
        /// <summary>
        /// The name of the sprite, or region, this frame refers to 
        /// </summary>
        public string Key { get; set; }
        /// <summary>
        /// Width, height and location of this frame on the sheet
        /// </summary>
        public Rect TextureRegion { get; set; }

        public bool Rotated { get; set; }
        public bool Trimmed { get; set; }
        /// <summary>
        /// Not entirely goddamned clear about what this one does or is or how it maps between sheet types
        /// </summary>
        public Rect SpriteSourceSize { get; set; }

        public Size SourceSize { get; set; }
    }
}
