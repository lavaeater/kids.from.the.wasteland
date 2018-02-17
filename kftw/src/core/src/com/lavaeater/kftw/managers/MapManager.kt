package com.lavaeater.kftw.managers

import com.badlogic.gdx.math.MathUtils

class MapManager {

    val mapStructure = mutableMapOf<Pair<Int, Int>, Tile>()

    val directions = mapOf(
//            Pair(-1, -1) to "northeast",
            Pair(-1,0) to "east",
//            Pair(-1, 1) to "southeast",
            Pair(0, 1) to "south",
//            Pair(1, 1) to "southwest",
            Pair(1, 0) to "west",
//            Pair(1, -1) to "northwest",
            Pair(0, -1) to "north"
    )

    val terrains = mapOf(
            0 to "water",
            1 to "grass",
            2 to "desert",
            3 to "rock")

    /*
    Lets just make terrain 2 x 2! - this means that a tile represents a
    2 x 2 grid when rendering!
     */

    init {
        var tileType = "water"
        for (x in -20..20)
            for(y in -20..20)
            {
                val priority = MathUtils.random.nextInt(4)
                tileType = terrains[priority]!!
                val x1 = x * 2
                val x2 = if(x < 0) x * 2 + 1 else x * 2 - 1

                val y1 = y * 2
                val y2 = if(y < 0) y * 2 + 1 else y * 2 - 1

                for(actualX in x1..x2)
                    for(actualY in y1..y2) {
                        val key = Pair(actualX, actualY)

                        val subType = "center${MathUtils.random.nextInt(3) + 1}"
                        mapStructure[key] =  Tile(key, priority, tileType, subType)
                    }
            }

        mapStructure.forEach {
            setExtraSprites(it.value)
        }
    }

    fun setExtraSprites(ourTile:Tile){
        val nTiles = getNeighbours(ourTile.key)

        val diffTiles = nTiles.filter { it.tileType != ourTile.tileType && it.priority > ourTile.priority }

        val extraSprites: MutableMap<String, String> = mutableMapOf()
        for (nTile in diffTiles)
        {
            // this is only tiles that a) differ and b) have higher priority.
            //figure out their direction and add these as extra features on every tile!
            val directionPair = Pair(ourTile.key.first - nTile.key.first, ourTile.key.second - nTile.key.second )
            val directionString = directions[directionPair]
            if(directionString != null) {
                ourTile.extraSprites.put(nTile.tileType, directionString)
            }
        }
    }

    fun getNeighbours(key: Pair<Int, Int>) : Collection<Tile> {
        val returnValue = mutableListOf<Pair<Int, Int>>()
        for(x in -1..1)
            (-1..1)
                    .asSequence()
                    .filter { x != 0 || it != 0 }
                    .forEach { returnValue.add(Pair(key.first + x, key.second + it)) }

        return mapStructure.filterKeys { returnValue.contains(it)}.values
    }
}

data class Tile(val key: Pair<Int, Int>, val priority : Int, val tileType:String, val subType: String, val extraSprites : MutableMap<String, String> = mutableMapOf())