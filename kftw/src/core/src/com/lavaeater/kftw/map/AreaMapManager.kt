package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import kotlin.system.measureTimeMillis

class AreaMapManager(world: World) : MapManagerBase(world) {

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

    init {

        generateTilesFor(0,0)
    }

    override fun generateTilesFor(xCenter:Int, yCenter:Int) {
        var tileType: String
        val newTiles = mutableListOf<TileKey>()
        for (x in -numberOfTiles..numberOfTiles)
            for (y in -numberOfTiles..numberOfTiles) {
                val offsetX = x + xCenter
                val offsetY = y + yCenter
                val key = TileKey(offsetX, offsetY)
                if(!currentMap.containsKey(key)) {
                    newTiles.add(key)
                    val nX = offsetX / scale
                    val nY = offsetY / scale
                    val priority = getTilePriorityFromNoise(nX, nY)
                    tileType = terrains[priority]!!
                    val code = shortTerrains[priority]!!
                    val subType = "center${MathUtils.random.nextInt(3) + 1}"
                    val possibleNewTile = Tile(priority, tileType, subType, code, code)
                    val newHashCode = possibleNewTile.hashCode()
                    if (!crazyTileStructure.containsKey(newHashCode)) {
                        crazyTileStructure.put(newHashCode, possibleNewTile)
                    }
                    currentMap.put(key, newHashCode)
                }
            }
        val newWay = measureTimeMillis {
            newTiles.forEach {
                setCode(it)
            }
        }
       newTiles.clear()
    }
}