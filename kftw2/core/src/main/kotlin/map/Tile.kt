package map

import com.badlogic.gdx.graphics.g2d.Sprite

enum class TileFog {
  NotSeen,
  Seen,
  Seeing
}

/**
 * Why another tile class? Well, to serialize
 * the maps, we need some extra data for our serialization except the data in the
 * Tile class. Some refactoring might be in place later...
 *
 * Should perhaps the Persisted tile contain features and stuff?
 *
 * I dunno...
 *
 * Should maps have layers, per se?
 *
 * I dunno.
 *
 * Later for that one...
 */
data class PersistedTile(val priority : Int,
                         val tileType:String,
                         val subType: String,
                         val code :String = "",
                         val shortCode : String = "",
                         val seen:Boolean)

fun TileInstance.persist() : PersistedTile {
  return PersistedTile(tile.priority, tile.tileType, tile.subType, tile.code, tile.shortCode, seen)
}

fun PersistedTile.tile() : Tile {
  return Tile(priority, tileType, subType, code, shortCode, false)
}

data class Tile (
    val priority : Int,
    val tileType:String,
    val subType: String,
    var code :String ="",
    var shortCode : String = "",
    var needsNeighbours : Boolean = true)

//Super fast renderable class for tiles
data class TileInstance(
    val x:Int,
    val y:Int,
    var baseSprite:Sprite,
    var extraSprites: Array<Sprite>,
    var needsHitBox: Boolean = true,
    var seen: Boolean = false,
    var seeing: Boolean = false,
    var tile: Tile,
    var blinking:Boolean = false) {
  val fogStatus: TileFog get() =  if(seeing) TileFog.Seeing else if(!seeing && !seen) TileFog.NotSeen else TileFog.Seen
}