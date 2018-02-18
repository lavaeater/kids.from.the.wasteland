using System.Text;
using SpriteSheetManagerLibraries.Interfaces;

namespace SpriteSheetManagerLibraries.Converters
{
    public class TexturePackerConverter : ConverterBase
    {
        public override bool CanRead => false;
        public override bool CanWrite => true;
        public override string FileFilter => $"Texturepacker (*.{FileExtension}) | *.{FileExtension}";
        public override string FileExtension => "txp";

        public override ISpriteSheet FromString(string spriteSheetData)
        {
            throw new System.NotImplementedException();
        }

        public override string ToString(ISpriteSheet spriteSheet)
        {
            var sb = new StringBuilder();
            GetMeta(spriteSheet, sb);
            GetRegions(spriteSheet, sb);
            return sb.ToString();
        }


        public void GetMeta(ISpriteSheet spriteSheet, StringBuilder sb)
        {
            sb.AppendLine(spriteSheet.ImageFileName);
            sb.AppendLine($"size: {spriteSheet.Size.Width}, {spriteSheet.Size.Height}");
            sb.AppendLine($"format: {spriteSheet.Format}");
            sb.AppendLine("filter: Linear, Linear");
            sb.AppendLine("repeat: none");
            /*
             * return "$fileName\n" +
                "size: $width, $height\n" +
                "format: RGBA8888\n" +
                "filter: Linear,Linear\n" +
                "repeat: none\n"
             */

        }

        public void GetRegions(ISpriteSheet spriteSheet, StringBuilder sb)
        {
            spriteSheet.Frames.ForEach(f=> GetRegion(f, sb));

        }

        private void GetRegion(ISpriteSheetFrame frame, StringBuilder sb)
        {
            /*
             * return "${name.removeSuffix(".png")}\n" +
                "  rotate: ${frame.rotated}\n" +
                "  xy: ${frame.frame.x}, ${frame.frame.y}\n" +
                "  size: ${frame.sourceSize.w}, ${frame.sourceSize.h}\n" +
                "  orig: ${frame.sourceSize.w}, ${frame.sourceSize.h}\n" +
                "  offset: 0, 0\n" + //Offset is the offset of the center of the sprite? Whaaat? For tiles, top left
                "  index: -1\n"
             */

            sb.AppendLine($"{frame.Key.Replace(".png", "")}");
            sb.AppendLine($"  rotate: {frame.Rotated}");
            sb.AppendLine($"  xy: {frame.TextureRegion.X}, {frame.TextureRegion.Y}");
            sb.AppendLine($"  size: {frame.SourceSize.Width}, {frame.SourceSize.Height}");
            sb.AppendLine($"  orig: {frame.SourceSize.Width}, {frame.SourceSize.Height}");
            sb.AppendLine($"  offset: 0, 0");
            sb.AppendLine($"  index: -1");
        }
    }
}