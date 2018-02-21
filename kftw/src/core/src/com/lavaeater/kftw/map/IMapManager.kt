package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3

interface IMapManager {
  fun getVisibleTiles(position: Vector3): Map<TileKey, Tile>
  fun getTileForPosition(position: Vector3): Tile
  fun getTileAt(x: Int, y: Int): Tile
  fun findTileOfType(x: Int, y: Int, tileType: String, range: Int): TileKey?
  fun getTilesInRange(posKey: TileKey, range: Int) : Map<TileKey, Tile>
}