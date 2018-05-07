package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector2
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.util.SimplexNoise
import kotlin.math.absoluteValue

fun String.toShortCode() : String {
  return "${this[0]}${this[1]}${this[3]}${this[5]}${this[7]}"
}

fun getNoise(x: Int, y: Int, vararg frequencies: Double): Double {
    val noiseVal = frequencies.sumByDouble { it * (SimplexNoise.noise(x.toDouble() * (1 / it), y.toDouble() * (1 / it))).absoluteValue }
    return noiseVal
}

fun getNoiseNotAbs(x: Int, y: Int, vararg frequencies: Double): Double {
    return frequencies.sumByDouble { it * (SimplexNoise.noise(x.toDouble() * (1 / it), y.toDouble() * (1 / it))) }
}

fun getNoiseNotAbs(x: Float, y: Float, vararg frequencies: Double): Double {
    return frequencies.sumByDouble { it * (SimplexNoise.noise(x * (1 / it), y * (1 / it))) }
}

fun String.isOneTerrain() : Boolean {
  return this == "ggggg" || this == "ddddd" || this == "wwwww" || this == "rrrrr"
}

fun Int.getMinMax(range:Int) : Pair<Int, Int> {
    return Pair(this - range, this + range)
}

fun getTilePriorityFromNoise(x: Float, y: Float): Int {

    val noiseValue = (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100)
    var priority = 0

    //Hmm, most likely this distribution is not from -1 .. 1 but more like -.75..0.75

    if (noiseValue in -100..-65)
        priority = 0
    if (noiseValue in -64..25)
        priority = 1
    if (noiseValue in 26..55)
        priority = 2
    if (noiseValue in 56..99)
        priority = 3

    return priority
}

fun Pair<Int,Int>.tileWorldCenter(tileSize:Int = GameManager.TILE_SIZE) : Vector2 {
  val x = (first.toFloat() * tileSize - tileSize / 2)
  val y = (second.toFloat() * tileSize - tileSize / 2)
  return Vector2(x,y)
}

fun Int.getMin(range:Int) : Int {
    return this - range
}

fun Int.coordAtDistanceFrom(range:Int) : Int {
    return this + range
}

fun Pair<Int,Int>.isInRange(x:Int, y:Int, range: Int) : Boolean {
    return this.isInRange(x.getMin(range),
            x.coordAtDistanceFrom(range),
            y.getMin(range),
            y.coordAtDistanceFrom(range))
}

fun Pair<Int,Int>.isInRange(minX:Int, maxX:Int, minY:Int, maxY:Int): Boolean{
    return (this.first in (minX)..(maxX) && this.second in (minY)..(maxY))
}

fun Pair<Int,Int>.isInRange(pos : Pair<Int,Int>, range : Int) : Boolean {
  return this.isInRange(pos.first, pos.second, range)
}

//fun MutableMap<TileKey, Int>.getTileKeyForDirection(key: TileKey, directionKey: TileKey): TileKey {
//  return Ctx.context.inject<TileKeyManager>().tileKey(key.x + directionKey.x, key.y + directionKey.y)
//}