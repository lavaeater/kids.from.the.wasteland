package com.lavaeater.kftw.map

data class TileKey(val x:Int, val y:Int)
data class Tile(val priority : Int, val tileType:String, val subType: String, var code :String ="", var shortCode : String = "")
