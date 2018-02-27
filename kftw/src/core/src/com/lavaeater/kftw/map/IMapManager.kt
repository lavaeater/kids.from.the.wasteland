package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3

interface IMapManager {
  fun getVisibleTiles(position: Vector3): Map<TileKey, Tile>
  fun tileForWorldPosition(position: Vector3): Tile
  fun getTileAt(x: Int, y: Int): Tile
    fun getTileAt(key: TileKey):Tile
  fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileKey?
    fun findTileOfTypeInRange(key: TileKey, tileType: String, range: Int): TileKey?
  fun getTilesInRange(posKey: TileKey, range: Int) : Map<TileKey, Tile>
    fun getRingOfTiles(tileKey: TileKey, range: Int): List<TileKey>
  fun generateTilesFor(xCenter: Int, yCenter: Int)
}