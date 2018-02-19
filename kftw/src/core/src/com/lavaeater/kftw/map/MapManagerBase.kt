package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.WorldManager
import com.lavaeater.kftw.systems.toTile
import kotlin.math.roundToInt

abstract class MapManagerBase: IMapManager {

    companion object {
        val weirdDirections = mapOf(
                "southwest" to "southwest",
                "westsouth" to "southwest",
                "northwest" to "northwest",
                "westnorth" to "northwest",
                "southeast" to "southeast",
                "eastsouth" to "southeast",
                "northeast" to "northeast",
                "eastnorth" to "northeast"
        )

        val complexDirections = mapOf(
                Pair(-1, -1) to "southwest",
                Pair(-1, 1) to "southeast",
                Pair(1, 1) to "northeast",
                Pair(1, -1) to "northwest")
        val simpleDirections = mapOf(
                Pair(-1,0) to "east",
                Pair(0, 1) to "south",
                Pair(1, 0) to "west",
                Pair(0, -1) to "north"
        )
        val simpleDirectionsInverse = mapOf(
                "east" to Pair(-1,0),
                "south" to Pair(0, 1),
                "west" to Pair(1, 0),
                "north" to Pair(0, -1))
        val terrains = mapOf(
                0 to "water",
                2 to "grass",
                1 to "desert",
                3 to "rock")
    }

    //This should really be up to every implementation of a mapmanager
    val mapStructure = mutableMapOf<TileKey, Tile>()








    val widthInTiles = (WorldManager.VIEWPORT_WIDTH / WorldManager.TILE_SIZE).roundToInt() + 5
    val heightInTiles = (WorldManager.VIEWPORT_HEIGHT / WorldManager.TILE_SIZE).roundToInt() + 5
    var currentKey = Pair(-100,-100)
    val visibleTiles = mutableMapOf<TileKey, Tile>()

    fun getSubType() : String {
        return "center${MathUtils.random.nextInt(3) + 1}"
    }

    fun doWeNeedNewVisibleTiles(position: Vector3) : Boolean {
        val centerTileKey = position.toTile(WorldManager.TILE_SIZE)
        return !centerTileKey.isInRange(currentKey, 2)
    }

    fun setExtraSprites(ourTile: Tile) {
        if (!ourTile.extraSpritesInitialized) {
            //Do not get neighbours. Loop over them instead!
            //Make a map for every tile, this will contain the correct stuff!


            val nTiles = getNeighbours(ourTile.key)

            val diffTiles = nTiles.filter { it.tileType != ourTile.tileType && it.priority > ourTile.priority }

            val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

            for (diffTile in diffTiles) {
                val directionPair = getDirection(ourTile.key, diffTile.key)
                if (simpleDirections.containsKey(directionPair)) {
                    val diffDirection = simpleDirections[directionPair]!!
                    //Bam, just add it, then clean it up afterwards!
                    if(ourTile.extraSprites.any { it.first == diffTile.tileType }) {
                        for (extraSprite in ourTile.extraSprites.filter { it.first == diffTile.tileType }) {

                            /*
                        This type of tile exists, it might actually be relevant to
                        remove the existing one in favor of this one!
                         */
                            if (weirdDirections.containsKey("$diffDirection${extraSprite.second}")) {
                                //Modify existing one, making it weird!
                                extraSpritesToRemove.add(extraSprite)
                                ourTile.extraSprites.add(Pair(extraSprite.first, weirdDirections["$diffDirection${extraSprite.second}"]!!))
                            } else {
                                ourTile.extraSprites.add(Pair(diffTile.tileType, diffDirection))
                            }
                        }
                    } else {
                        ourTile.extraSprites.add(Pair(diffTile.tileType, diffDirection))
                    }
                }

            }
            for(extraSprite in extraSpritesToRemove) {
                ourTile.extraSprites.remove(extraSprite)
            }
            ourTile.extraSpritesInitialized = true
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

//    open fun getNeighbours(key: Pair<Int, Int>) : Collection<Tile> {
//        val returnValue = mutableListOf<Pair<Int, Int>>()
//        for(x in -1..1)
//            (-1..1)
//                    .asSequence()
//                    .filter { x != 0 && it != 0 }
//                    .forEach { returnValue.add(Pair(key.first + x, key.second + it)) }
//
//        return mapStructure.filterKeys { returnValue.contains(it)}.values
//    }

    open fun getNeighbours(key:Pair<Int, Int>) : List<Tile> {
        return simpleDirectionsInverse.keys.mapNotNull { mapStructure.getNeighbourTileAt(key, it) }
    }
}



fun MutableMap<Pair<Int, Int>, Tile>.getNeighbourTileAt(key : Pair<Int, Int>, direction: String): Tile? {
    val dirKey = MapManagerBase.simpleDirectionsInverse[direction]!!
    val neighbourKey = Pair(key.first + dirKey.first, key.second + dirKey.second)
    return this[neighbourKey]
}