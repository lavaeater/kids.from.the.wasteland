using System.Collections.Generic;
using System.Windows;
using SpriteSheetManagerLibraries.Interfaces;

namespace SpriteSheetManagerLibraries.Models
{
    public class SpriteSheet : ISpriteSheet
    {
        public string ImageFileName { get; set; }
        public string BaseDir { get; set; }
        public string Format { get; set; }
        public Size Size { get; set; }
        public string Application { get; set; }
        public string Version { get; set; }
        public List<ISpriteSheetFrame> Frames { get; set; } = new List<ISpriteSheetFrame>();
    }
}
