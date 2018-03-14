package com.lavaeater.kftw.map

enum class TileFog {
  NotSeen,
  Seen,
  Seeing
}

data class TileKey(val x:Int, val y:Int)
data class Tile(val priority : Int, val tileType:String, val subType: String, var code :String ="", var shortCode : String = "")

data class RenderableTile(val key: TileKey, val tile: Tile, val fogStatus: TileFog)
