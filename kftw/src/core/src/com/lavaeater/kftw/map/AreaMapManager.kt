package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.WorldManager
import com.lavaeater.kftw.systems.toTile

class AreaMapManager : MapManagerBase() {

    fun getTilePriorityFromNoise(x:Float, y:Float) : Int {

        val noiseValue = (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100)
        var priority = 0

        //Hmm, most likely this distribution is not from -1 .. 1 but more like -.75..0.75

        if(noiseValue in -100..-65)
            priority = 0
        if(noiseValue in -64..25)
            priority = 1
        if(noiseValue in 26..55)
            priority = 2
        if(noiseValue in 56..99)
            priority = 3

        return priority
    }

    val scale = 10.0f

    init {
        var tileType = "water"
        for (x in -50..50)
            for(y in -50..50)
            {
                val nX = x / scale
                val nY = y / scale
                val priority = getTilePriorityFromNoise(nX,nY)
                tileType = terrains[priority]!!
//                val x1 = x * 2
//                val x2 = if(x < 0) x * 2 + 1 else x * 2 - 1
//                val y1 = y * 2
//                val y2 = if(y < 0) y * 2 + 1 else y * 2 - 1
//                for(actualX in x1..x2)
//                    for(actualY in y1..y2) {
                        val key = Pair(x, y)
                        val subType = "center${MathUtils.random.nextInt(3) + 1}"
                        mapStructure[key] =  Tile(key, priority, tileType, subType)
//                    }
            }
        mapStructure.forEach {
            setExtraSprites(it.value)
        }
    }


    override fun getVisibleTiles(position: Vector3): List<Tile> {
        if(doWeNeedNewVisibleTiles(position)) {
            visibleTiles.clear()
            currentKey = position.toTile(WorldManager.TILE_SIZE)
            //Our map is actually static. We can just filter the structure on the keys!
            val minX = currentKey.first.coordAtDistanceFrom(-widthInTiles)
            val maxX = currentKey.first.coordAtDistanceFrom(widthInTiles)
            val minY = currentKey.second.coordAtDistanceFrom(-widthInTiles)
            val maxY = currentKey.second.coordAtDistanceFrom(widthInTiles)

            for (x in minX..maxX)
                (minY..maxY)
                        .map { Pair(x, it) }
                        .filter { mapStructure.containsKey(it) }
                        .forEach { visibleTiles.add(mapStructure[it]!!) }
        }
        return visibleTiles
    }
}