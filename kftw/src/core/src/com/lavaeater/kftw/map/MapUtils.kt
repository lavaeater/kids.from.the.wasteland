package com.lavaeater.kftw.map

data class Tile(val key: Pair<Int, Int>, val priority : Int, val tileType:String, val subType: String, var extraSpritesInitialized : Boolean = false, val extraSprites : MutableMap<String, String> = mutableMapOf())

fun Pair<Int, Int>.isInRange(key : Pair<Int, Int>, range: Int) : Boolean {
    val minX = key.first - range
    val maxX = key.first + range
    val minY = key.second - range
    val maxY = key.second + range
    return (this.first in (minX)..(maxX) && this.second in (minY)..(maxY))
}