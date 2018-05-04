package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector2
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.util.SimplexNoise
import map.TileKeyManager
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

fun TileKey.tileWorldCenter(tileSize:Int = GameManager.TILE_SIZE) : Vector2 {
  val x = (this.x.toFloat() * tileSize - tileSize / 2)
  val y = (this.y.toFloat() * tileSize - tileSize / 2)
  return Vector2(x,y)
}

fun Int.getMin(range:Int) : Int {
    return this - range
}

fun Int.coordAtDistanceFrom(range:Int) : Int {
    return this + range
}

fun TileKey.isInRange(key : TileKey, range: Int) : Boolean {
    return this.isInRange(key.x.getMin(range),
            key.x.coordAtDistanceFrom(range),
            key.y.getMin(range),
            key.y.coordAtDistanceFrom(range))
}

fun TileKey.isInRange(minX:Int, maxX:Int, minY:Int, maxY:Int): Boolean{
    return (this.x in (minX)..(maxX) && this.y in (minY)..(maxY))
}

fun MutableMap<TileKey, Int>.getTileKeyForDirection(key: TileKey, directionKey: TileKey): TileKey {
  return Ctx.context.inject<TileKeyManager>().tileKey(key.x + directionKey.x, key.y + directionKey.y)
}