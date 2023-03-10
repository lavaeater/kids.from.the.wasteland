package map

import com.badlogic.gdx.graphics.g2d.Sprite

enum class TileFog {
  NotSeen,
  Seen,
  Seeing
}

data class Tile(
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
    val baseSprite:Sprite,
    val extraSprites: Array<Sprite>,
    var needsHitBox: Boolean = true,
    var seen: Boolean = false,
    var seeing: Boolean = false,
    val tile: Tile) {
  val fogStatus: TileFog get() =  if(seeing) TileFog.Seeing else if(!seeing && !seen) TileFog.NotSeen else TileFog.Seen
}