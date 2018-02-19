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

        val simpleDirections = mapOf(
                TileKey(-1,0) to "east",
                TileKey(0, 1) to "south",
                TileKey(1, 0) to "west",
                TileKey(0, -1) to "north"
        )
        val simpleDirectionsInverse = mapOf(
                "east" to TileKey(-1,0),
                "south" to TileKey(0, 1),
                "west" to TileKey(1, 0),
                "north" to TileKey(0, -1))

        val terrains = mapOf(
                0 to "water",
                2 to "grass",
                1 to "desert",
                3 to "rock")
    }

    //This should really be up to every implementation of a mapmanager
    val crazyMapStructure = mutableMapOf<TileKey, Int>()
    val crazyTileStructure = mutableMapOf<Int, Tile>()

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

    fun setExtraSprites(ourKey: TileKey) {

        //Make a copy of this tile for comparison later!
        val tempTile = crazyTileStructure[crazyMapStructure[ourKey]]!!.copy(extraSprites = mutableListOf())

        val nTiles = getNeighbours(ourKey)

        val diffTiles = nTiles.filter { it.value.tileType != tempTile.tileType && it.value.priority > tempTile.priority }

        val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

        for (diffTile in diffTiles) {
            val directionPair = getNeighbourDirection(ourKey, diffTile.key)
            if (simpleDirections.containsKey(directionPair)) {
                val diffDirection = simpleDirections[directionPair]!!
                //Bam, just add it, then clean it up afterwards!
                if(tempTile.extraSprites.any { it.first == diffTile.value.tileType }) {
                    for (extraSprite in tempTile.extraSprites.filter { it.first == diffTile.value.tileType }) {

                        /*
                    This type of tile exists, it might actually be relevant to
                    remove the existing one in favor of this one!
                     */
                        if (weirdDirections.containsKey("$diffDirection${extraSprite.second}")) {
                            //Modify existing one, making it weird!
                            extraSpritesToRemove.add(extraSprite)
                            tempTile.extraSprites.add(Pair(extraSprite.first, weirdDirections["$diffDirection${extraSprite.second}"]!!))
                        } else {
                            tempTile.extraSprites.add(Pair(diffTile.value.tileType, diffDirection))
                        }
                    }
                } else {
                    tempTile.extraSprites.add(Pair(diffTile.value.tileType, diffDirection))
                }
            }

        }
        for(extraSprite in extraSpritesToRemove) {
            tempTile.extraSprites.remove(extraSprite)
        }
        //Now, check if the hashcodes still match!
        val newHashCode = tempTile.hashCode()
        if(crazyMapStructure[ourKey] != newHashCode){
            //Add this new tile to the tile storage!
            if(!crazyTileStructure.containsKey(newHashCode)) {
                crazyTileStructure.put(newHashCode, tempTile)
            }
            crazyMapStructure[ourKey] = newHashCode
        }
    }

    open fun createTile(key: Pair<Int, Int>) : Tile {
        return Tile(0, "water", "center1")
    }

    fun getNeighbourDirection(inputKey : TileKey, otherKey : TileKey) : TileKey {
        return TileKey(inputKey.x - otherKey.x, inputKey.y - otherKey.y)
    }

    open fun getNeighbours(inKey:TileKey) : Map<TileKey, Tile> {

        val some = simpleDirectionsInverse.map {(direction, key)-> crazyMapStructure.getTileKeyForDirection(inKey, direction, key)}

        //The mapValues function MUST return values, otherwise
        return crazyMapStructure.filter { entry -> some.contains(entry.key) }.mapValues{ crazyTileStructure[it.value]!! }
    }
}

fun MutableMap<TileKey, Int>.getTileKeyForDirection(key: TileKey, direction: String, directionKey: TileKey) : TileKey {
    val entryKey = TileKey(key.x + directionKey.x, key.y + directionKey.y)
    return entryKey
}