package com.lavaeater.kftw.map

import com.badlogic.gdx.graphics.g2d.Sprite

enum class TileFog {
  NotSeen,
  Seen,
  Seeing
}

data class TileKey(val x:Int, val y:Int)
data class Tile(val priority : Int, val tileType:String, val subType: String, var code :String ="", var shortCode : String = "")

//Super fast renderable class for tiles
data class TileInstance(val baseSprite:Sprite, val extraSprites: Array<Sprite>, val fogStatus: TileFog = TileFog.NotSeen)

data class RenderableTile(val key: TileKey, val tile: Tile, val fogStatus: TileFog)
