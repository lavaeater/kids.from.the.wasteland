package map

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.BodyFactory
import managers.GameManager
import com.lavaeater.kftw.map.Tile
import com.lavaeater.kftw.map.TileInstance
import com.lavaeater.kftw.systems.tileX
import com.lavaeater.kftw.systems.tileY
import ktx.math.vec2
import kotlin.math.roundToInt

class MapManager : IMapManager {
  override fun getBandOfTiles(tilePos: Pair<Int, Int>, range: Int, width: Int): List<TileInstance> {
    return getBandOfTiles(tilePos.first, tilePos.second, range, width)
  }

  override fun getBandOfTiles(x: Int, y: Int, range: Int, width: Int): List<TileInstance> {
        if (range < 1 || width < 1) return listOf()
    if (width == 1) return getRingOfTiles(x,y, range)

    val tilesInMaxRange = getTilesInRange(x,y, range + width)
    val tilesToExclude = getTilesInRange(x,y, range - 1)
    return tilesInMaxRange.minus(tilesToExclude).toList()
  }

  override fun getRingOfTiles(x: Int, y: Int, range: Int): List<TileInstance> {
    if (range < 1) return listOf()

    val tilesInRange = getTilesInRange(x,y, range)
    val tilesToExclude = getTilesInRange(x,y, range - 1)
    return tilesInRange.minus(tilesToExclude).toList()
  }

  val bodyManager = Ctx.context.inject<BodyFactory>()
  val tileManager = Ctx.context.inject<TileManager>()
  var currentlyVisibleTiles: Array<Array<TileInstance>>? = null

  //val inverseFogOfWar = mutableSetOf<TileKey>()
  val hitBoxes = mutableListOf<Body>()
  override var currentX = 0
  override var currentY = 0
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
            Pair(-1, 0) to "east",
        Pair(0, 1) to "south",
        Pair(1, 0) to "west",
        Pair(0, -1) to "north"
    )
    val simpleDirectionsInverse = mapOf(
        "north" to Pair(0, -1),
        "east" to Pair(-1, 0),
        "south" to Pair(0, 1),
        "west" to Pair(1, 0))

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

    val shortTerrainPriority = mapOf(
            'w' to 0,
            'd' to 1,
            'g' to 2,
            'r' to 3
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

    val scale = 80.0f

    val widthInTiles = (GameManager.VIEWPORT_WIDTH / GameManager.TILE_SIZE).roundToInt()
    val currentTileRange: Int = widthInTiles * 2
    val visibleRange = widthInTiles / 2
  }

  fun doWeNeedNewVisibleTiles(x:Int, y:Int): Boolean {
    return !(currentX in (x - visibleRange)..(x + visibleRange) && currentY in (y - visibleRange)..(y + visibleRange))
  }

  fun checkHitBoxesForImpassibleTiles() {

    for (row in currentlyVisibleTiles!!)
      for (tile in row) {
        if (tile.needsHitBox && (tile.tile.priority == 0 || tile.tile.priority == 3) && !tile.tile.shortCode.isOneTerrain()) {
          val pos = vec2((tile.x * GameManager.TILE_SIZE).toFloat() + GameManager.TILE_SIZE / 2,
              (tile.y * GameManager.TILE_SIZE).toFloat() + GameManager.TILE_SIZE / 2)
          val hitBox = bodyManager.createBody(
              GameManager.TILE_SIZE.toFloat(),
              GameManager.TILE_SIZE.toFloat(),
              10f,
              pos,
              BodyDef.BodyType.StaticBody)
            tile.needsHitBox = false
        }
      }
  }

  override fun getTileAt(x: Int, y: Int): Tile {
    return tileManager.getTile(x,y).tile
  }

  override fun findTileOfTypeInRange(x: Int, y: Int, tileType: String, range: Int): TileInstance? {
    val tilesInRange = getTilesInRange(x, y, range)
    return tilesInRange.filter { it.tile.tileType == tileType }.firstOrNull()
  }


  override fun tileForWorldPosition(position: Vector3): Tile {
    return tileManager.getTile(position.tileX(), position.tileY()).tile
  }

//  val tileCounter = Ctx.context.inject<PerformanceCounters>().add("TileGetting")
  override fun getVisibleTiles(x:Int, y:Int) : Array<Array<TileInstance>> {
//    tileCounter.start()
    if(currentlyVisibleTiles == null || doWeNeedNewVisibleTiles(x,y)) {
      currentX = x
      currentY = y
      currentlyVisibleTiles = tileManager.getTiles(
              (currentX - currentTileRange)..(currentX + currentTileRange),
              (currentY - currentTileRange)..(currentY + currentTileRange))
    }
  checkHitBoxesForImpassibleTiles()
//    tileCounter.stop()
    return currentlyVisibleTiles!!
  }

  override fun getVisibleTiles(position: Vector3): Array<Array<TileInstance>> {
    return getVisibleTiles(position.tileX(), position.tileY())
  }

  override fun getTilesInRange(x:Int, y:Int, range: Int): List<TileInstance> {
    val minX = x.coordAtDistanceFrom(-range)
    val maxX = x.coordAtDistanceFrom(range)
    val minY = y.coordAtDistanceFrom(-range)
    val maxY = y.coordAtDistanceFrom(range)

    return tileManager.getTiles(minX..maxX, minY..maxY).flatten()
  }
}
/*
The code below contains some fog of war-specific code. We're not there yet
 */


//  //This needs work to... work.
//  override fun getVisibleTilesWithFog(position: Vector3): List<RenderableTile> {
//    return listOf()
//    val tiles = getVisibleTiles(position)
//    val key = position.toTile()
//    return tiles.map { RenderableTile(it.key, it.value, getFogStatus(it.key, key)) }
//  }

//  private fun getFogStatus(key: TileKey, position: TileKey): TileFog {
//    //1.Everything within some radius of the player is currently seen
//    if (key.isInRange(position, 5)) {
//      if (!inverseFogOfWar.contains(key)) //Now it's seen!
//        inverseFogOfWar.add(key)
//      return TileFog.Seeing
//    }
//
//    //2. Everything that we have visited HAS been seen
//
//    //3. Everything else is unseen
//    return if (inverseFogOfWar.contains(key)) TileFog.Seen else TileFog.NotSeen
//  }