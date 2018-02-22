package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.systems.toTile

class AreaMapManager : MapManagerBase() {

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

    val scale = 20.0f
    val numberOfTiles = 50

    init {
        var tileType: String
        for (x in -numberOfTiles..numberOfTiles)
            for (y in -numberOfTiles..numberOfTiles) {
                val nX = x / scale
                val nY = y / scale
                val priority = getTilePriorityFromNoise(nX, nY)
                tileType = terrains[priority]!!
                val key = TileKey(x, y)
                val subType = "center${MathUtils.random.nextInt(3) + 1}"
                val possibleNewTile = Tile(priority, tileType, subType)
                val newHashCode = possibleNewTile.hashCode()
                if (!crazyTileStructure.containsKey(newHashCode)) {
                    crazyTileStructure.put(newHashCode, possibleNewTile)
                }
                crazyMapStructure.put(key, newHashCode)
            }

        crazyMapStructure.forEach {
            setExtraSprites(it.key)
        }
    }
}