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

            var terrainTypes = xDoc.Root.Element("terraintypes").Elements().ToDictionary(element => element.Attribute("name").Value, element => Int32.Parse(element.Attribute("tile").Value));

            spriteSheet.Application = "";

            spriteSheet.ImageFileName = _imageSource;
            spriteSheet.Size = new Size(_imageWidth, _imageHeight);
           

            int i = 0;
            foreach (var terrainType in terrainTypes)
            {
                var tiles = xDoc.Root.Elements("tile").Where(element => element.IsOfTerrainType(i));

                spriteSheet.Frames.AddRange(tiles.Select(t => TiledElementToSheetFrame(t, terrainType.Key)));

                i++;
            }

            return spriteSheet;
        }

        private SpriteSheetFrame TiledElementToSheetFrame(XElement tiled, string terrainType)
        {
            int imageIndex = Int32.Parse(tiled.Attribute("id").Value);
            int currentRow = imageIndex / _tilesPerRow;

            var location = new Point(imageIndex * _tileWidth, currentRow * _tileHeight);
            var size = new Vector(_tileWidth, _tileHeight);

            return new SpriteSheetFrame()
            {
                Key = terrainType,
                Rotated = false,
                SourceSize = new Size(_tileWidth, _tileHeight),
                SpriteSourceSize = new Rect(),
                TextureRegion = new Rect(location, size),
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
        public static Int32 GetTerrainType(this string terrain)
        {
            var stringVal = terrain.Split(',').First(s => !string.IsNullOrEmpty(s));

            return Int32.Parse(stringVal);
        }
    }
}
