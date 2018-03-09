package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.lavaeater.Assets
import com.lavaeater.kftw.managers.BodyFactory
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.systems.toTile
import ktx.math.vec2
import kotlin.math.roundToInt

abstract class MapManagerBase : IMapManager {

  val bodyManager = Ctx.context.inject<BodyFactory>()

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
        "north" to TileKey(0, -1),
        "east" to TileKey(-1, 0),
        "south" to TileKey(0, 1),
        "west" to TileKey(1, 0))

    val terrains = mapOf(
        0 to "water",
        1 to "desert",
        2 to "grass",
        3 to "rock")
    val terrainPriorities = mapOf(
        "water" to 0,
        "desert" to 1,
        "grass" to 2,
        "rock" to 3)

    val shortTerrains = mapOf(
        0 to "w",
        1 to "d",
        2 to "g",
        3 to "r"
    )

    val shortLongTerrains = mapOf(
        'w' to "water",
        'd' to "desert",
        'g' to "grass",
        'r' to "rock")

    val neiborMap = mapOf(
        Pair(0, 1) to "north",
        Pair(1, 1) to "northeast",
        Pair(1, 0) to "east",
        Pair(1, -1) to "southeast",
        Pair(0, -1) to "south",
        Pair(-1, -1) to "southwest",
        Pair(-1, 0) to "west",
        Pair(-1, 1) to "northwest")

    val noExtraSprites = hashSetOf<String>()

    val scale = 40.0f
    val numberOfTiles = 50
  }

  var currentMap = mutableMapOf<TileKey, Int>()
  val tiles = mutableMapOf<Int, Tile>()
  val inverseFogOfWar = mutableSetOf<TileKey>()
  val hitBoxes = mutableMapOf<TileKey, Body>()
  val widthInTiles = (GameManager.VIEWPORT_WIDTH / GameManager.TILE_SIZE).roundToInt() + 5
  var currentKey = TileKey(-100, -100) //Argh, we need to fix this, we assign and reassign all the time. Perhaps this should just be mutable? Nah - We should go for arrays
  val visibleTiles = mutableMapOf<TileKey, Tile>()

  fun doWeNeedNewVisibleTiles(position: Vector3): Boolean {
    val centerTileKey = position.toTile(GameManager.TILE_SIZE)
    return !centerTileKey.isInRange(currentKey, 5)
  }

  fun checkHitBoxesForImpassibleTiles() {
    val impassibleTiles = visibleTiles
        .filter {
          (it.value.priority == 0 || it.value.priority == 3) && !it.value.shortCode.isOneTerrain()
        }
        .filter {
          !hitBoxes.containsKey(it.key)
        }

    for (key in impassibleTiles.keys) {
      val pos = vec2((key.x * GameManager.TILE_SIZE).toFloat() + GameManager.TILE_SIZE / 2,
          (key.y * GameManager.TILE_SIZE).toFloat() + GameManager.TILE_SIZE / 2)
      val hitBox = bodyManager.createBody(
          GameManager.TILE_SIZE.toFloat(),
          GameManager.TILE_SIZE.toFloat(),
          10f,
          pos,
          BodyDef.BodyType.StaticBody)
      hitBoxes[key] = hitBox
    }
  }

  fun addEdgeSpritesForTile(ourKey: TileKey, shortCode: String, tileType: String, priority: Int) {

    if (!noExtraSprites.contains(shortCode) && !Assets.codeToExtraTiles.containsKey(shortCode)) {
      val extraSprites = mutableListOf<Pair<String, String>>()

      val nTiles = getNeighbours(ourKey)

      val diffTiles = nTiles.filter { it.value.tileType != tileType && it.value.priority > priority }

      val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

      for (diffTile in diffTiles) {
        val directionPair = getDirectionOfNeighbour(ourKey, diffTile.key)
        if (simpleDirections.containsKey(directionPair)) {
          val diffDirection = simpleDirections[directionPair]!!
          //Bam, just add it, then clean it up afterwards!
          if (extraSprites.any { it.first == diffTile.value.tileType }) {
            for (extraSprite in extraSprites.filter { it.first == diffTile.value.tileType }) {

              /*
        This type of tile exists, it might actually be relevant to
        remove the existing one in favor of this one!
         */
              if (weirdDirections.containsKey("$diffDirection${extraSprite.second}")) {
                //Modify existing one, making it weird!
                extraSpritesToRemove.add(extraSprite)
                extraSprites.add(Pair(extraSprite.first, weirdDirections["$diffDirection${extraSprite.second}"]!!))
              } else {
                extraSprites.add(Pair(diffTile.value.tileType, diffDirection))
              }
            }
          } else {
            extraSprites.add(Pair(diffTile.value.tileType, diffDirection))
          }
        }
      }
      for (extraSprite in extraSpritesToRemove) {
        extraSprites.remove(extraSprite)
      }

      if (extraSprites.any())
        Assets.codeToExtraTiles.put(shortCode, extraSprites.map { Assets.sprites[it.first]!![it.second]!! })
      else
        noExtraSprites.add(shortCode)
    }
  }

  fun generateCodeForTile(ourKey: TileKey) {

    val tempTile = tiles[currentMap[ourKey]]!!.copy()
    neiborMap.keys.map { (x, y) ->
      tiles[currentMap[TileKey(ourKey.x + x, ourKey.y + y)]]
    }
        .forEach { tempTile.code += if (it != null) shortTerrains[it.priority]!! else "b" }

    tempTile.shortCode = tempTile.code.toShortCode()

    val newHashCode = tempTile.hashCode()
    if (currentMap[ourKey] != newHashCode) {
      //Add this new tile to the tile storage!
      if (!tiles.containsKey(newHashCode)) {
        tiles.put(newHashCode, tempTile)
      }
      currentMap[ourKey] = newHashCode
    }
    addEdgeSpritesForTile(ourKey, tempTile.shortCode, tempTile.tileType, tempTile.priority)
  }

  fun getDirectionOfNeighbour(inputKey: TileKey, otherKey: TileKey): TileKey {
    return TileKey(inputKey.x - otherKey.x, inputKey.y - otherKey.y)
  }

  override fun getTileAt(x: Int, y: Int): Tile {
    return tiles[currentMap[TileKey(x, y)]]!!
  }

  override fun getTileAt(key: TileKey): Tile {
    return tiles[currentMap[key]]!!
  }

  override fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileKey? {
    return findTileOfTypeInRange(TileKey(x, y), tileType, range)
  }

  override fun findTileOfTypeInRange(key: TileKey, tileType: String, range: Int): TileKey? {
    val tilesInRange = getTilesInRange(key, range)
    return tilesInRange.filter { it.value.tileType == tileType }.keys.firstOrNull()
  }

  open fun getNeighbours(inKey: TileKey): Map<TileKey, Tile> {

    val some = simpleDirectionsInverse.values.map { currentMap.getTileKeyForDirection(inKey, it) }

    //The mapValues function MUST return values, otherwise
    return currentMap.filter { entry -> some.contains(entry.key) }.mapValues { tiles[it.value]!! }
  }

  override fun tileForWorldPosition(position: Vector3): Tile {
    val tileKey = position.toTile(GameManager.TILE_SIZE)
    return tiles[currentMap[tileKey]]!!
  }

  override fun getVisibleTilesWithFog(position: Vector3): List<RenderableTile> {
    val tiles = getVisibleTiles(position)
    val key = position.toTile()
    return tiles.map { RenderableTile(it.key, it.value, getFogStatus(it.key, key)) }
  }

  private fun getFogStatus(key: TileKey, position: TileKey): TileFog {
    //1.Everything within some radius of the player is currently seen
    if (key.isInRange(position, 5)) {
      if (!inverseFogOfWar.contains(key)) //Now it's seen!
        inverseFogOfWar.add(key)
      return TileFog.Seeing
    }

    //2. Everything that we have visited HAS been seen

    //3. Everything else is unseen
    return if (inverseFogOfWar.contains(key)) TileFog.Seen else TileFog.NotSeen

  }

  override fun getVisibleTiles(position: Vector3): Map<TileKey, Tile> {
    if (doWeNeedNewVisibleTiles(position)) {
      currentKey = position.toTile(GameManager.TILE_SIZE)
      visibleTiles.clear()
      val range = (widthInTiles * 1.5).roundToInt()
      var vbt = getTilesInRange(currentKey, range)
      if (vbt.size < (range * 2 * range * 2) - range) {
        generateTilesFor(currentKey.x, currentKey.y)
        vbt = getTilesInRange(currentKey, widthInTiles)
      }
      visibleTiles.putAll(vbt)
      checkHitBoxesForImpassibleTiles()
    }
    return visibleTiles
  }

  override fun getRingOfTiles(tileKey: TileKey, range: Int): List<TileKey> {
    if (range < 1) return listOf()

    val tilesInRange = getTilesInRange(tileKey, range)
    val tilesToExclude = getTilesInRange(tileKey, range - 1)
    return tilesInRange.keys.minus(tilesToExclude.keys).toList()
  }

  override fun getBandOfTiles(tileKey: TileKey, range: Int, width: Int): List<TileKey> {
    if (range < 1 || width < 1) return listOf()
    if (width == 1) return getRingOfTiles(tileKey, range)

    val tilesInMaxRange = getTilesInRange(tileKey, range + width)
    val tilesToExclude = getTilesInRange(tileKey, range - 1)
    return tilesInMaxRange.keys.minus(tilesToExclude.keys).toList()
  }

  override fun getTilesInRange(posKey: TileKey, range: Int): Map<TileKey, Tile> {
    val minX = posKey.x.coordAtDistanceFrom(-range)
    val maxX = posKey.x.coordAtDistanceFrom(range)
    val minY = posKey.y.coordAtDistanceFrom(-range)
    val maxY = posKey.y.coordAtDistanceFrom(range)

    return currentMap.filter { it.key.x in minX..maxX && it.key.y in minY..maxY }
        .mapValues { tiles[it.value]!! }
  }
}
