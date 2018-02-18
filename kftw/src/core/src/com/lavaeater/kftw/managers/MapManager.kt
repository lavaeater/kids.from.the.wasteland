package com.lavaeater.kftw.managers

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.systems.toTile
import com.lavaeater.kftw.util.SimplexNoise
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class MapManager {

    val mapStructure = mutableMapOf<Pair<Int, Int>, Tile>()

    val complexDirections = mapOf(
                        Pair(-1, -1) to "northeast",
              Pair(-1, 1) to "southeast",
            Pair(1, 1) to "southwest",
            Pair(1, -1) to "northwest")

    val simpleDirections = mapOf(
            Pair(-1,0) to "east",
            Pair(0, 1) to "south",
            Pair(1, 0) to "west",
            Pair(0, -1) to "north"
    )

    val terrains = mapOf(
            0 to "water",
            1 to "grass",
            2 to "desert",
            3 to "rock")


    fun getTileForKey(key: Pair<Int,Int>, createIfNotExists : Boolean = true):Tile? {
        if(mapStructure.containsKey(key)) return mapStructure[key]!!
        if(createIfNotExists) {
            mapStructure[key] = createTileWithPerlinNoise(key)
        }
        return mapStructure[key]
    }

    private fun createTileWithPerlinNoise(key: Pair<Int, Int>): Tile {
        val randomInt = (getNoise(key.first, key.second, 1.0,0.5) * 100).roundToInt()
        var priority = 0
        if(randomInt in 0..10)
            priority = 0
        if(randomInt in 11..70)
            priority = 1
        if(randomInt in 71..85)
            priority = 2
        if(randomInt in 85..99)
            priority = 3
        val tileType = terrains[priority]!!
        val subType = getSubType()
        val tile = Tile(key, priority, tileType, subType)

        return tile
    }

    fun getNoise(x: Int, y: Int, vararg frequencies: Double): Double {
        val noiseVal = frequencies.sumByDouble { it * (SimplexNoise.noise(x.toDouble() * (1/ it), y.toDouble() * (1/ it))).absoluteValue }
        return noiseVal
    }

    val widthInTiles = (WorldManager.VIEWPORT_WIDTH / WorldManager.TILE_SIZE).roundToInt() + 5
    val heightInTiles = (WorldManager.VIEWPORT_HEIGHT / WorldManager.TILE_SIZE).roundToInt() + 5
    var currentKey = Pair(-100,-100)
    val visibleTiles = mutableListOf<Tile>()

    fun getVisibleTiles(position: Vector3) : List<Tile> {
        val centerTileKey = position.toTile(WorldManager.TILE_SIZE)
        if(!centerTileKey.isInRange(currentKey, 2)) {
            currentKey = centerTileKey
            visibleTiles.clear()
            val startX = currentKey.first - widthInTiles
            val stopX = currentKey.first + widthInTiles
            val startY = currentKey.second - heightInTiles
            val stopY = currentKey.second + heightInTiles
            for (x in startX..stopX)
            for(y in startY..stopY) {
                visibleTiles.add(getTileForKey(Pair(x, y))!!)
            }
        }
        visibleTiles.filter { !it.extraSpritesInitialized }.forEach { setExtraSprites(it) }
        return visibleTiles
    }

    /*
    Lets just make terrain 2 x 2! - this means that a tile represents a
    2 x 2 grid when rendering!
     */

    fun createTile(key: Pair<Int, Int>) : Tile {
        val randomInt = MathUtils.random.nextInt(100)
        var priority = 0
        if(randomInt in 0..60)
            priority = 0
        if(randomInt in 61..80)
            priority = 1
        if(randomInt in 81..90)
            priority = 2
        if(randomInt in 91..99)
            priority = 3
        val tileType = terrains[priority]!!
        val subType = getSubType()
        val tile = Tile(key, priority, tileType, subType)

        return tile
    }

    fun getSubType() : String {
        return "center${MathUtils.random.nextInt(3) + 1}"
    }

//    init {
//        var tileType = "water"
//        for (x in -20..20)
//            for(y in -20..20)
//            {
//                val priority = MathUtils.random.nextInt(4)
//                tileType = terrains[priority]!!
//                val x1 = x * 2
//                val x2 = if(x < 0) x * 2 + 1 else x * 2 - 1
//
//                val y1 = y * 2
//                val y2 = if(y < 0) y * 2 + 1 else y * 2 - 1
//
//                for(actualX in x1..x2)
//                    for(actualY in y1..y2) {
//                        val key = Pair(actualX, actualY)
//
//                        val subType = getSubType()
//                        mapStructure[key] =  Tile(key, priority, tileType, subType)
//                    }
//            }
//
//        mapStructure.forEach {
//            setExtraSprites(it.value)
//        }
//    }

    fun setExtraSprites(ourTile:Tile){
        if(!ourTile.extraSpritesInitialized) {
            val nTiles = getNeighboursDynamic(ourTile.key)

            val diffTiles = nTiles.filter { it.tileType != ourTile.tileType && it.priority > ourTile.priority }

            /*
        Hmm. First find out if it is more than one tile that borders and is

         */

            if (diffTiles.any()) {
                if (diffTiles.count() == 1) {
                    val nTile = diffTiles.first()
                    val directionPair = getDirection(ourTile.key, nTile.key)
                    if (simpleDirections.containsKey(directionPair)) {
                        val directionString = simpleDirections[directionPair]!!
                        ourTile.extraSprites.put(nTile.tileType, directionString)
                    }
                } else {
                    val tileTypes = diffTiles.map { it.tileType }
                    for (tileType in tileTypes) {
                        val sameTiles = diffTiles.filter { it.tileType == tileType }
                        if (sameTiles.count() == 1) {
                            val nTile = diffTiles.first()
                            val directionPair = getDirection(ourTile.key, nTile.key)
                            if (simpleDirections.containsKey(directionPair)) {
                                val directionString = simpleDirections[directionPair]!!
                                ourTile.extraSprites.put(tileType, directionString)
                            }
                        } else {
                            /*
                        if these two tiles are in the same direction, we need to add
                        a special extra sprite for that tile
                         */
                            if (sameTiles.any { getDirection(ourTile.key, it.key).first == -1 } &&
                                    sameTiles.any { getDirection(ourTile.key, it.key).second == -1 }) {
                                val directionString = complexDirections[Pair(-1, -1)]!!
                                ourTile.extraSprites.put(tileType, directionString)
                            }
                            if (sameTiles.any { getDirection(ourTile.key, it.key).first == -1 } &&
                                    sameTiles.any { getDirection(ourTile.key, it.key).second == 1 }) {
                                val directionString = complexDirections[Pair(-1, 1)]!!
                                ourTile.extraSprites.put(tileType, directionString)
                            }
                            if (sameTiles.any { getDirection(ourTile.key, it.key).first == 1 } &&
                                    sameTiles.any { getDirection(ourTile.key, it.key).second == 1 }) {
                                val directionString = complexDirections[Pair(1, 1)]!!
                                ourTile.extraSprites.put(tileType, directionString)
                            }
                            if (sameTiles.any { getDirection(ourTile.key, it.key).first == 1 } &&
                                    sameTiles.any { getDirection(ourTile.key, it.key).second == -1 }) {
                                val directionString = complexDirections[Pair(1, -1)]!!
                                ourTile.extraSprites.put(tileType, directionString)
                            }
                        }
                    }
                }
            }
            ourTile.extraSpritesInitialized = nTiles.count() == 8
        }

//        for (nTile in diffTiles)
//        {
//            // this is only tiles that a) differ and b) have higher priority.
//            //figure out their direction and add these as extra features on every tile!
//            val directionPair = Pair(ourTile.key.first - nTile.key.first, ourTile.key.second - nTile.key.second )
//            val directionString = simpleDirections[directionPair]!!
//            ourTile.extraSprites.put(nTile.tileType, directionString)
//        }
    }

    fun getDirection(inputKey : Pair<Int, Int>, otherKey : Pair<Int, Int>) : Pair<Int, Int> {
        return Pair(inputKey.first - otherKey.first, inputKey.second - otherKey.second )
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

    fun getNeighboursDynamic(key: Pair<Int, Int>) : List<Tile> {
        val mutableList = mutableListOf<Tile>()
        for (x in -1..1)
            (-1..1)
                    .asSequence()
                    .filter { x != 0 || it != 0 }
                    .forEach {
                        var tile = getTileForKey(Pair(key.first + x, key.second + it), false)
                        if(tile != null) mutableList.add(tile)
                    }
        return mutableList
    }
}

fun Pair<Int, Int>.isInRange(key : Pair<Int, Int>, range: Int) : Boolean {
    val minX = key.first - range
    val maxX = key.first + range
    val minY = key.second - range
    val maxY = key.second + range
    return (this.first in (minX)..(maxX) && this.second in (minY)..(maxY))
}

data class Tile(val key: Pair<Int, Int>, val priority : Int, val tileType:String, val subType: String, var extraSpritesInitialized : Boolean = false, val extraSprites : MutableMap<String, String> = mutableMapOf())