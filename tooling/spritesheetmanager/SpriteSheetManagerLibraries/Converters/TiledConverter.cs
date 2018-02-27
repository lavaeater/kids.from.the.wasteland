using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.Models;

namespace SpriteSheetManager.Converters
{
    public class TiledConverter : ConverterBase
    {
        private int _tilesPerRow;
        public override bool CanRead => true;
        public override bool CanWrite => false;
        public override string FileFilter => $"PixiJS (*.{FileExtension})|*.{FileExtension}";
        public override string FileExtension => "tsx";
        public override ISpriteSheet FromString(string spriteSheetData)
        {
            var xDoc = XDocument.Parse(spriteSheetData);
            var spriteSheet = new SpriteSheet();

            var tileWidth = Int32.Parse(xDoc.Root.Element("tileset").Attribute("tilewidth").Value);
            var tileHeight = Int32.Parse(xDoc.Root.Element("tileset").Attribute("tileheight").Value);

            var imageSource = xDoc.Root.Element("image").Attribute("source").Value;
            var imageWidth = Int32.Parse(xDoc.Root.Element("image").Attribute("width").Value);
            var imageHeight = Int32.Parse(xDoc.Root.Element("image").Attribute("height").Value);

            _tilesPerRow = imageWidth / tileWidth;

            var terrainTypes = xDoc.Root.Element("terraintypes").Elements().ToDictionary(element => element.Attribute("name").Value, element => Int32.Parse(element.Attribute("tile").Value));

            var tiles = xDoc.Root.Elements("tile").Select(TiledElementToSheetFrame);

            foreach (var terrainType in terrainTypes)
            {
                //Get the tiles for this terraintype?
                var id = terrainType.Value;
            }

            return spriteSheet;
        }

        private SpriteSheetFrame TiledElementToSheetFrame(XElement tiled)
        {
            int imageIndex = Int32.Parse(tiled.Attribute("id").Value);
            int currentRow = imageIndex / _tilesPerRow;

            int terrainType = tiled.Attribute("terrain").Value.GetTerrainType();



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
