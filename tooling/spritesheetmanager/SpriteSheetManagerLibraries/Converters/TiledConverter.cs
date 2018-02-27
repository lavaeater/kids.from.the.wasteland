using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Xml.Linq;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public class TiledConverter : ConverterBase
    {
        private int _tilesPerRow;
        private int _tileWidth;
        private int _tileHeight;
        private string _imageSource;
        private int _imageWidth;
        private int _imageHeight;
        private Dictionary<int, string> _terrainTypes = new Dictionary<int, string>();
        public override bool CanRead => true;
        public override bool CanWrite => false;
        public override string FileFilter => $"PixiJS (*.{FileExtension})|*.{FileExtension}";
        public override string FileExtension => "tsx";
        public override ISpriteSheet FromString(string spriteSheetData)
        {
            var xDoc = XDocument.Parse(spriteSheetData);
            var spriteSheet = new SpriteSheet();

            _tileWidth = Int32.Parse(xDoc.Element("tileset").Attribute("tilewidth").Value);
            _tileHeight = Int32.Parse(xDoc.Element("tileset").Attribute("tileheight").Value);

            _imageSource = xDoc.Root.Element("image").Attribute("source").Value;
            _imageWidth = Int32.Parse(xDoc.Root.Element("image").Attribute("width").Value);
            _imageHeight = Int32.Parse(xDoc.Root.Element("image").Attribute("height").Value);

            _tilesPerRow = _imageWidth / _tileWidth;

            _terrainTypes = xDoc.Root.Element("terraintypes").Elements()
                .Select((element, i) => (i, element.Attribute("name").Value)).ToDictionary(tuple => tuple.Item1, tuple => tuple.Item2); 
            spriteSheet.Application = "";

            spriteSheet.ImageFileName = _imageSource;
            spriteSheet.Size = new Size(_imageWidth, _imageHeight);

            spriteSheet.Frames.AddRange(xDoc.Root.Elements("tile").Select(TiledElementToSheetFrame));

            return spriteSheet;
        }

        private SpriteSheetFrame TiledElementToSheetFrame(XElement tiled, int imageIndex)
        {
//            int imageIndex = Int32.Parse(tiled.Attribute("id").Value) - 1;
            int currentRow = imageIndex / _tilesPerRow;
            int currentColumn = imageIndex - (currentRow * _tilesPerRow);

            var topLeft = new Point(currentColumn * _tileWidth, currentRow * _tileHeight);
            var lowerRight = new Point(currentColumn * _tileWidth + _tileWidth, currentRow * _tileHeight + _tileHeight);

            return new SpriteSheetFrame()
            {
                Key = _terrainTypes[tiled.GetTerrainType()],
                Rotated = false,
                SourceSize = new Size(_tileWidth, _tileHeight),
                SpriteSourceSize = new Rect(),
                TextureRegion = new Rect(topLeft, lowerRight),
                Trimmed = false
            };
        }

        public override string ToString(ISpriteSheet spriteSheet)
        {
            throw new NotImplementedException();
        }
    }

    public static class TiledHelper
    {
        public static bool IsOfTerrainType(this XElement tile, int terrainType)
        {
            return tile.Attribute("terrain").Value.GetTerrainType() == terrainType;
        }

        public static Int32 GetTerrainType(this XElement tile)
        {
            return tile.Attribute("terrain").Value.GetTerrainType();
        }

        public static Int32 GetTerrainType(this string terrain)
        {
            var stringVal = terrain.Split(',').First(s => !string.IsNullOrEmpty(s));

            return Int32.Parse(stringVal);
        }
    }
}
