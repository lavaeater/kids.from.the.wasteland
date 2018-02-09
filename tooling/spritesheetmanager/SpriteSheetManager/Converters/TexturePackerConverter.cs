using SpriteSheetManager.Interfaces;

namespace SpriteSheetManager.Converters
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
            throw new System.NotImplementedException();
        }
    }
}