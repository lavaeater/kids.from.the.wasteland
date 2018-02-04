using System.Windows;
using SpriteSheetManager.Interfaces;

namespace SpriteSheetManager.Models
{
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