package map

import com.badlogic.gdx.math.Vector3

interface IMapManager {
  fun getVisibleTilesWithFog(x:Int, y:Int, range:Int): Array<TileInstance>
  fun getVisibleTiles(position: Vector3): Array<Array<TileInstance>>
  fun tileForWorldPosition(position: Vector3): Tile
  fun getTileAt(x: Int, y: Int): Tile
  fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileInstance?
  fun getTilesInRange(x: Int, y:Int, range:Int): List<TileInstance>
  fun getRingOfTiles(x:Int, y:Int, range: Int): List<TileInstance>
  fun getBandOfTiles(tilePos: Pair<Int,Int>, range:Int, width: Int = 1): List<TileInstance>
  fun getBandOfTiles(x:Int, y:Int, range: Int, width: Int = 1): List<TileInstance>
  fun getVisibleTiles(x: Int, y: Int): Array<Array<TileInstance>>
  val currentX:Int
  val currentY:Int
}