package com.lavaeater.kftw.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.systems.toTile
import kotlin.math.roundToInt

abstract class MapManagerBase : IMapManager {

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
        TileKey(-1, 0) to "east",
        TileKey(0, 1) to "south",
        TileKey(1, 0) to "west",
        TileKey(0, -1) to "north"
    )
    val simpleDirectionsInverse = mapOf(
        "east" to TileKey(-1, 0),
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
  var currentMap = mutableMapOf<TileKey, Int>()

  /*
  Naah, fuck the arrays.
  For now.

  I will instead use a map of mapstructures, how fucking insane is THAT?
   */

  val crazyTileStructure = mutableMapOf<Int, Tile>()

  val widthInTiles = (GameManager.VIEWPORT_WIDTH / GameManager.TILE_SIZE).roundToInt() + 5
  val heightInTiles = (GameManager.VIEWPORT_HEIGHT / GameManager.TILE_SIZE).roundToInt() + 5
  var currentKey = TileKey(-100, -100) //Argh, we need to fix this, we assign and reassign all the time. Perhaps this should just be mutable? Nah - We should go for arrays
  val visibleTiles = mutableMapOf<TileKey, Tile>()
  val scale = 40.0f
  val numberOfTiles = 25
  val neibOr =
      mapOf(
        -1 to 0,
        1 to 0,
        0 to -1,
        0 to 1)

  fun getSubType(): String {
    return "center${MathUtils.random.nextInt(3) + 1}"
  }

  fun doWeNeedNewVisibleTiles(position: Vector3): Boolean {
    val centerTileKey = position.toTile(GameManager.TILE_SIZE)
    return !centerTileKey.isInRange(currentKey, 2)
  }

  fun setExtraSprites(ourKey: TileKey) {

    //Make a copy of this tile for comparison later!
    val tempTile = crazyTileStructure[currentMap[ourKey]]!!.copy(extraSprites = mutableListOf())

    val nTiles = getNeighbours(ourKey)

    val diffTiles = nTiles.filter { it.value.tileType != tempTile.tileType && it.value.priority > tempTile.priority }

    val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

    for (diffTile in diffTiles) {
      val directionPair = getNeighbourDirection(ourKey, diffTile.key)
      if (simpleDirections.containsKey(directionPair)) {
        val diffDirection = simpleDirections[directionPair]!!
        //Bam, just add it, then clean it up afterwards!
        if (tempTile.extraSprites.any { it.first == diffTile.value.tileType }) {
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
    for (extraSprite in extraSpritesToRemove) {
      tempTile.extraSprites.remove(extraSprite)
    }
    //Now, check if the hashcodes still match!
    val newHashCode = tempTile.hashCode()
    if (currentMap[ourKey] != newHashCode) {
      //Add this new tile to the tile storage!
      if (!crazyTileStructure.containsKey(newHashCode)) {
        crazyTileStructure.put(newHashCode, tempTile)
      }
      currentMap[ourKey] = newHashCode
    }
  }

  fun setCode(ourKey: TileKey) {
    val tempTile = crazyTileStructure[currentMap[ourKey]]!!.copy(code = "")
    neibOr.mapNotNull {(x,y) ->
      crazyTileStructure[currentMap[TileKey(ourKey.x + x, ourKey.y + y)]] }
        .forEach { tempTile.code += it.priority }
  }

  fun getNeighbourDirection(inputKey: TileKey, otherKey: TileKey): TileKey {
    return TileKey(inputKey.x - otherKey.x, inputKey.y - otherKey.y)
  }

  override fun getTileAt(x: Int, y: Int): Tile {
    return crazyTileStructure[currentMap[TileKey(x, y)]]!!
  }

  override fun getTileAt(key: TileKey): Tile {
    return crazyTileStructure[currentMap[key]]!!
  }

  override fun findTileOfType(x: Int, y: Int, tileType: String, range: Int): TileKey? {
    return findTileOfType(TileKey(x, y), tileType, range)
  }

  override fun findTileOfType(key: TileKey, tileType: String, range: Int): TileKey? {
    val tilesInRange = getTilesInRange(key, range)
    return tilesInRange.filter { it.value.tileType == tileType }.keys.firstOrNull()
  }

  open fun getNeighbours(inKey: TileKey): Map<TileKey, Tile> {

    val some = simpleDirectionsInverse.values.map { currentMap.getTileKeyForDirection(inKey, it) }

    //The mapValues function MUST return values, otherwise
    return currentMap.filter { entry -> some.contains(entry.key) }.mapValues { crazyTileStructure[it.value]!! }
  }

  override fun getTileForPosition(position: Vector3): Tile {
    val tileKey = position.toTile(GameManager.TILE_SIZE)
    return crazyTileStructure[currentMap[tileKey]]!!
  }

  override fun getVisibleTiles(position: Vector3): Map<TileKey, Tile> {
    if (doWeNeedNewVisibleTiles(position)) {
      currentKey = position.toTile(GameManager.TILE_SIZE)
      visibleTiles.clear()
      val range = (widthInTiles * 0.75).roundToInt()
      var vbt = getTilesInRange(currentKey, range)
      if(vbt.size < (range * 2  * range * 2) - range)
      {
        generateTilesFor(currentKey.x, currentKey.y)
        vbt = getTilesInRange(currentKey, widthInTiles)
      }
      visibleTiles.putAll(vbt)
    }
    return visibleTiles
  }

  override fun getRingOfTiles(tileKey: TileKey, range: Int): List<TileKey> {
    if (range < 1) return listOf()

    val tilesInRange = getTilesInRange(tileKey, range)
    val tilesToExclude = getTilesInRange(tileKey, range - 1)
    return tilesInRange.keys.minus(tilesToExclude.keys).toList()
  }

  override fun getTilesInRange(posKey: TileKey, range: Int): Map<TileKey, Tile> {

    val tilesInRange = mutableMapOf<TileKey, Tile>()
    val minX = posKey.x.coordAtDistanceFrom(-range)
    val maxX = posKey.x.coordAtDistanceFrom(range)
    val minY = posKey.y.coordAtDistanceFrom(-range)
    val maxY = posKey.y.coordAtDistanceFrom(range)

    for (x in minX..maxX)
      (minY..maxY)
          .map { TileKey(x, it) }
          .filter { currentMap.containsKey(it) }
          .forEach { tilesInRange.put(it, crazyTileStructure[currentMap[it]!!]!!) }
    return tilesInRange
  }
}

fun MutableMap<TileKey, Int>.getTileKeyForDirection(key: TileKey, directionKey: TileKey): TileKey {
  val entryKey = TileKey(key.x + directionKey.x, key.y + directionKey.y)
  return entryKey
}