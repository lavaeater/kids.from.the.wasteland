package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.WorldManager
import com.lavaeater.kftw.systems.toTile
import kotlin.math.roundToInt

abstract class MapManagerBase: IMapManager {

    //This should really be up to every implementation of a mapmanager
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
            2 to "grass",
            1 to "desert",
            3 to "rock")
    val widthInTiles = (WorldManager.VIEWPORT_WIDTH / WorldManager.TILE_SIZE).roundToInt() + 5
    val heightInTiles = (WorldManager.VIEWPORT_HEIGHT / WorldManager.TILE_SIZE).roundToInt() + 5
    var currentKey = Pair(-100,-100)
    val visibleTiles = mutableListOf<Tile>()
    val visibleMap = mutableMapOf<Pair<Int,Int>, Tile>()

    fun getSubType() : String {
        return "center${MathUtils.random.nextInt(3) + 1}"
    }

    fun doWeNeedNewVisibleTiles(position: Vector3) : Boolean {
        val centerTileKey = position.toTile(WorldManager.TILE_SIZE)
        return !centerTileKey.isInRange(currentKey, 2)
    }

    fun setExtraSprites(ourTile: Tile){
        if(!ourTile.extraSpritesInitialized) {
            val nTiles = getNeighbours(ourTile.key)

            val diffTiles = nTiles.filter { it.tileType != ourTile.tileType && it.priority > ourTile.priority }

            if (diffTiles.any()) {
                if (diffTiles.count() == 1) {
                    val diffTile = diffTiles.first()
                    fixOneDiffTile(diffTile, ourTile)
                } else {
                    val tileTypes = diffTiles.map { it.tileType }

                    for (tileType in tileTypes) {
                        val sameTiles = diffTiles.filter { it.tileType == tileType }

                        if (sameTiles.count() == 1) {
                            val sameTile = sameTiles.first()
                            //Just this one was actually a silly bug!
                            fixOneDiffTile(sameTile, ourTile)
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
            } else {
                ourTile.extraSpritesInitialized = true
            }
            if(!ourTile.extraSpritesInitialized) ourTile.extraSpritesInitialized = nTiles.count() == 8
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

    protected fun fixOneDiffTile(diffTile: Tile, ourTile: Tile) {
        val directionPair = getDirection(ourTile.key, diffTile.key)
        if (simpleDirections.containsKey(directionPair)) {
            val directionString = simpleDirections[directionPair]!!
            ourTile.extraSprites.put(diffTile.tileType, directionString)
        }
    }

    open fun createTile(key: Pair<Int, Int>) : Tile {
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

    fun getDirection(inputKey : Pair<Int, Int>, otherKey : Pair<Int, Int>) : Pair<Int, Int> {
        return Pair(inputKey.first - otherKey.first, inputKey.second - otherKey.second )
    }

    open fun getNeighbours(key: Pair<Int, Int>) : Collection<Tile> {
        val returnValue = mutableListOf<Pair<Int, Int>>()
        for(x in -1..1)
            (-1..1)
                    .asSequence()
                    .filter { x != 0 || it != 0 }
                    .forEach { returnValue.add(Pair(key.first + x, key.second + it)) }

        return mapStructure.filterKeys { returnValue.contains(it)}.values
    }

}