package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3
data class TileRange(val topX:Int, val topY:Int, val bottomX:Int, val bottomY:Int)

interface IMapManager {
  fun getVisibleTilesWithFog(position: Vector3): List<RenderableTile>
  fun getVisibleTiles(position: Vector3): Map<TileKey, Tile>
  fun tileForWorldPosition(position: Vector3): Tile
  fun getTileAt(x: Int, y: Int): Tile
  fun getTileAt(key: TileKey): Tile
  fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileKey?
  fun findTileOfTypeInRange(key: TileKey, tileType: String, range: Int): TileKey?
  fun getTilesInRange(x: Int, y:Int, range:Int): Map<TileKey, Tile>
  fun getTilesInRange(posKey: TileKey, range: Int): Map<TileKey, Tile>
  fun getRingOfTiles(tileKey: TileKey, range: Int): List<TileKey>
  fun getBandOfTiles(tileKey:TileKey, range: Int, width: Int = 1): List<TileKey>
  fun generateTilesFor(xCenter: Int, yCenter: Int)
}