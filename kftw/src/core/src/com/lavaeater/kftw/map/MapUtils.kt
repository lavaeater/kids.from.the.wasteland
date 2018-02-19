package com.lavaeater.kftw.map

import com.lavaeater.kftw.util.SimplexNoise
import kotlin.math.absoluteValue

data class TileKey(val x:Int, val y:Int)
data class Tile(val priority : Int, val tileType:String, val subType: String, var extraSpritesInitialized : Boolean = false, val extraSprites : MutableList<Pair<String, String>> = mutableListOf())

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

fun Pair<Int, Int>.getXRange(range:Int) : Pair<Int,Int> {
    return this.first.getMinMax(range)
}

fun Pair<Int, Int>.getYRange(range:Int) : Pair<Int,Int> {
    return this.second.getMinMax(range)
}

fun Int.getMinMax(range:Int) : Pair<Int, Int> {
    return Pair(this - range, this + range)
}

fun Int.getMin(range:Int) : Int {
    return this - range
}

fun Int.coordAtDistanceFrom(range:Int) : Int {
    return this + range
}

fun Pair<Int, Int>.isInRange(key : Pair<Int, Int>, range: Int) : Boolean {
    return this.isInRange(key.first.getMin(range),
            key.first.coordAtDistanceFrom(range),
            key.second.getMin(range),
            key.second.coordAtDistanceFrom(range))
}

fun Pair<Int, Int>.isInRange(minX:Int, maxX:Int, minY:Int, maxY:Int): Boolean{
    return (this.first in (minX)..(maxX) && this.second in (minY)..(maxY))
}