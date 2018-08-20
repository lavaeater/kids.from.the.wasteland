package map

import Assets
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import injection.Ctx
import ktx.math.vec3
import managers.GameManager
import util.SimplexNoise
import kotlin.math.absoluteValue

fun String.toShortCode() : String {
  return "${this[0]}${this[1]}${this[3]}${this[5]}${this[7]}"
}



/* SEARCH FOR THIS */

fun getNoise(x: Int, y: Int, vararg frequencies: Double): Double {
    val noiseVal = frequencies.sumByDouble { it * (SimplexNoise.noise(x.toDouble() * (1 / it), y.toDouble() * (1 / it))).absoluteValue }
    return noiseVal
}

fun getNoiseNotAbs(x: Int, y: Int, vararg frequencies: Double): Double {
    return frequencies.sumByDouble { it * (SimplexNoise.noise(x.toDouble() * (1 / it), y.toDouble() * (1 / it))) }
}

fun getNoiseNotAbs(x: Float, y: Float, vararg frequencies: Double): Double {
    return frequencies.sumByDouble { it * (SimplexNoise.noise(x * (1 / it), y * (1 / it))) }
}

fun String.isOneTerrain() : Boolean {
  return this == "ggggg" || this == "ddddd" || this == "wwwww" || this == "rrrrr"
}

//fun TileInstance.getBox() : Rectangle {
//
//  /*
//  if tile in say, north, direction, is of HIGHER priority, then that northern tile is
//  encroaching on this one -> the hitbox becomes smaller
//  if it is of lower priority, then THIS tile encroaches on THAT tile - the hitbox grows!
//
//  So, we'll try with a hitbox growth / shrinkage of say 1/4 tile size.
//   */
//  val charArray = this.tile.shortCode.toCharArray()
//  val thisType = charArray[0]
//  val typeNorth = charArray[1]
//  if(MapService.shortTerrainPriority[typeNorth]!! > MapService.shortTerrainPriority[thisType]!!) {
//
//  }
//
//  val height = GameManager.TILE_SIZE
////  if()
//}

enum class Directions {
  NORTH,
  EAST,
  SOUTH,
  WEST
}

class DirectionPos {
  companion object {
    val dirPos = mapOf(
        Directions.NORTH to 1,
        Directions.EAST to 2,
        Directions.SOUTH to 3,
        Directions.WEST to 4
        )
  }
}

fun Int.getMinMax(range:Int) : Pair<Int, Int> {
    return Pair(this - range, this + range)
}

fun getNoiseValue(x:Float, y:Float): Int {
  return (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100).toInt()
}

fun getTilePriorityFromNoise(x: Float, y: Float, tileX:Int, tileY:Int): Int {

  val distanceFactor = 0//Math.min((tileX.absoluteValue + tileY.absoluteValue) / (40.0), 1.0)

  val part1 = distanceFactor * -100
  val part2 = (1 - distanceFactor) * (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100)

  val noiseValue = part1 + part2
  //Gdx.app.log("testTag","$x, $y, ${noiseValue} $distanceFactor")

  //val factor = (x.absoluteValue + y.absoluteValue) * 0.5
  //val noiseValue = (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100).toFloat() - factor
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

fun Pair<Int,Int>.tileWorldCenter(tileSize:Int = GameManager.TILE_SIZE) : Vector2 {
  val x = (first.toFloat() * tileSize - tileSize / 2)
  val y = (second.toFloat() * tileSize - tileSize / 2)
  return Vector2(x,y)
}

fun getWorldScreenCoordinats(x:Int, y:Int, tileSize: Int = GameManager.TILE_SIZE): Vector3 {
  return Ctx.context.inject<Camera>().project(vec3(x.toFloat() * tileSize - tileSize / 2 , y.toFloat() * tileSize - tileSize / 2,0f))
}

fun Int.getMin(range:Int) : Int {
    return this - range
}

fun Int.coordAtDistanceFrom(range:Int) : Int {
    return this + range
}

fun Pair<Int,Int>.isInRange(x:Int, y:Int, range: Int) : Boolean {
    return this.isInRange(x.getMin(range),
            x.coordAtDistanceFrom(range),
            y.getMin(range),
            y.coordAtDistanceFrom(range))
}

object CircleCache {
  private val circle = Circle(0f,0f, 5f)
  private var currentX = 0
  private var currentY = 0
  private var currentRadius = 5
  fun getCircle(x:Int, y: Int,radius: Int) : Circle {
    if(currentX != x || currentY != y || currentRadius != radius) {
      circle.setPosition(x.toFloat(), y.toFloat())
      circle.setRadius(radius.toFloat())
    }
    return circle
  }
}

fun Pair<Int, Int>.isInCircle(x:Int, y:Int, radius: Int) : Boolean {
  return CircleCache.getCircle(x,y, radius).contains(this.first.toFloat(), this.second.toFloat())
}

fun isInCircle(x:Int, y:Int, circleX:Int, circleY:Int, radius:Int) : Boolean {
  return CircleCache.getCircle(circleX, circleY, radius).contains(x.toFloat(), y.toFloat())
}


fun Pair<Int,Int>.isInRange(minX:Int, maxX:Int, minY:Int, maxY:Int): Boolean{
    return (this.first in (minX)..(maxX) && this.second in (minY)..(maxY))
}

fun Pair<Int,Int>.isInRange(pos : Pair<Int,Int>, range : Int) : Boolean {
  return this.isInRange(pos.first, pos.second, range)
}

fun Tile.getKeyCode() : String {
  return this.priority.toString() + this.tileType + this.subType + this.code + this.shortCode + this.needsNeighbours
}

fun Tile.getInstance(x:Int, y:Int): TileInstance {
  return TileInstance(x, y, this.getSprite(), this.getExtraSprites(), this.isImpassible(), tile = this)
}

fun Tile.updateInstance(tileInstance: TileInstance) {
	tileInstance.baseSprite = this.getSprite()
	tileInstance.extraSprites = this.getExtraSprites()
	tileInstance.needsHitBox = this.isImpassible()
	tileInstance.tile = this
}

fun Tile.isImpassible() : Boolean {
  return (this.priority == 0 || this.priority == 3) && !this.shortCode.isOneTerrain()
}

fun Tile.getSprite() : Sprite {
  return Assets.tileSprites[this.tileType]!![this.subType]!!
}

fun Tile.getExtraSprites() : Array<Sprite> {
  if (Assets.codeToExtraTiles.containsKey(this.shortCode))
    return Assets.codeToExtraTiles[this.shortCode]!!.toTypedArray()
  return emptyArray()
}

fun TileInstance.forwardIs(thisDirection:String, tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>): Boolean {
  val forward = MapService.simpleForward[thisDirection]!!
  val forwardKey = Pair(this.x + forward.first, this.y + forward.second)
  return tilesByKey.containsKey(forwardKey) && tilesByKey[forwardKey]?.tile?.tileType == tileType
}

fun TileInstance.leftRightAndForwardAre(thisDirection:String, tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>): Boolean {
  var bothAre = true
  val left = MapService.simpleLeft[thisDirection]!!
  val leftKey = Pair(this.x + left.first, this.y + left.second)
  bothAre = bothAre && tilesByKey.containsKey(leftKey) && tilesByKey[leftKey]?.tile?.tileType == tileType
  val right = MapService.simpleRight[thisDirection]!!
  val rightKey = Pair(this.x + right.first, this.y + right.second)
  bothAre = bothAre && tilesByKey.containsKey(rightKey) && tilesByKey[rightKey]?.tile?.tileType == tileType
  val forward = MapService.simpleForward[thisDirection]!!
  val forwardKey = Pair(this.x + forward.first, this.y + forward.second)
  bothAre = bothAre && tilesByKey.containsKey(forwardKey) && tilesByKey[forwardKey]?.tile?.tileType == tileType

  return bothAre
}

fun TileInstance.leftRightAndForwardAreNot(thisDirection:String, tileTypes: Set<String>, tilesByKey: Map<Pair<Int, Int>, TileInstance>): Boolean {
  var bothAre = true
  val left = MapService.simpleLeft[thisDirection]!!
  val leftKey = Pair(this.x + left.first, this.y + left.second)
  bothAre = bothAre && tilesByKey.containsKey(leftKey) && !tileTypes.contains(tilesByKey[leftKey]?.tile?.tileType)
  val right = MapService.simpleRight[thisDirection]!!
  val rightKey = Pair(this.x + right.first, this.y + right.second)
  bothAre = bothAre && tilesByKey.containsKey(rightKey) && !tileTypes.contains(tilesByKey[rightKey]?.tile?.tileType)
  val forward = MapService.simpleForward[thisDirection]!!
  val forwardKey = Pair(this.x + forward.first, this.y + forward.second)
  bothAre = bothAre && tilesByKey.containsKey(forwardKey) && !tileTypes.contains(tilesByKey[forwardKey]?.tile?.tileType)

  return bothAre
}

fun TileInstance.inFrontAreAll(direction: String, tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>) : Boolean {
  val directions = MapService.infront[direction]!!.map { MapService.directions[it]!! }
  var allAre = true

  for(direction in directions) {
    val currentKey = Pair(this.x + direction.first, this.y + direction.second)
    allAre = allAre && tilesByKey.containsKey(currentKey) && tilesByKey[currentKey]!!.tile.tileType == tileType
  }
  return allAre
}

fun TileInstance.inFrontAreNone(direction: String, tileTypes: Set<String>, tilesByKey: Map<Pair<Int, Int>, TileInstance>) : Boolean {
  val directions = MapService.infront[direction]!!.map { MapService.directions[it]!! }
  var nonAre = true

  for(direction in directions) {
    val currentKey = Pair(this.x + direction.first, this.y + direction.second)
    nonAre = nonAre &&  tilesByKey.containsKey(currentKey) && !tileTypes.contains(tilesByKey[currentKey]!!.tile.tileType)
  }
  return nonAre
}


fun TileInstance.hasAtLeastTwoLeftRightForward(thisDirection:String, tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>): Boolean {
  var count = 0

  val left = MapService.simpleLeft[thisDirection]!!
  val leftKey = Pair(this.x + left.first, this.y + left.second)

  if(tilesByKey.containsKey(leftKey) && tilesByKey[leftKey]?.tile?.tileType == tileType)
    count++

  val right = MapService.simpleRight[thisDirection]!!
  val rightKey = Pair(this.x + right.first, this.y + right.second)

  if(tilesByKey.containsKey(rightKey) && tilesByKey[rightKey]?.tile?.tileType == tileType)
    count++

  var forward = MapService.simpleForward[thisDirection]!!
  var forwardKey = Pair(this.x + forward.first, this.y + forward.second)

  if(tilesByKey.containsKey(forwardKey) && tilesByKey[forwardKey]?.tile?.tileType == tileType)
    count++

  forwardKey = Pair(forwardKey.first + forward.first, forwardKey.second + forward.second)
  if(tilesByKey.containsKey(forwardKey) && tilesByKey[forwardKey]?.tile?.tileType == tileType)
    count++


  return count > 2
}

fun TileInstance.leftAndRightAre(thisDirection:String, tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>): Boolean {
  var bothAre = true
  val left = MapService.simpleLeft[thisDirection]!!
  val leftKey = Pair(this.x + left.first, this.y + left.second)
  bothAre = bothAre && tilesByKey.containsKey(leftKey) && tilesByKey[leftKey]?.tile?.tileType == tileType
  val right = MapService.simpleRight[thisDirection]!!
  val rightKey = Pair(this.x + left.first, this.y + left.second)
  bothAre = bothAre && tilesByKey.containsKey(rightKey) && tilesByKey[rightKey]?.tile?.tileType == tileType

  return bothAre
}

fun TileInstance.neighbourToIs(direction:String, tileType:String, tilesByKey: Map<Pair<Int, Int>, TileInstance>):Boolean {
  val key = MapService.directions[direction]!!
  val actualKey = Pair(this.x + key.first, this.y + key.first)
  return tilesByKey[actualKey]?.tile?.tileType == tileType
}

fun TileInstance.atLEastOneNeighbourIs(tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>) :Boolean {
  var noAreOfType = false
  for(coord in MapService.neighbourMap.keys) {
    val key = Pair(this.x + coord.first, this.y + coord.second)
    noAreOfType = noAreOfType || (tilesByKey.containsKey(key) && tilesByKey[key]!!.tile.tileType == tileType)
  }
  return noAreOfType
}

fun TileInstance.noNeighboursAre(tileType: String, tilesByKey: Map<Pair<Int, Int>, TileInstance>) :Boolean {
  var noAreOfType = true
  for(coord in MapService.neighbourMap.keys) {
    val key = Pair(this.x + coord.first, this.y + coord.second)
    noAreOfType = noAreOfType && tilesByKey.containsKey(key) && tilesByKey[key]!!.tile.tileType != tileType
  }
  return noAreOfType
}

fun TileInstance.hasBothAsNeighbours(tileTypes: Set<String>, tilesByKey: Map<Pair<Int, Int>, TileInstance>) : Boolean {

  var containsCount = 0
  for(tileType in tileTypes) {
    var hasAllAsNeighbours = false
    for(coord in MapService.neighbourMap.keys) {
      val key = Pair(this.x + coord.first, this.y + coord.second)
      hasAllAsNeighbours = hasAllAsNeighbours || tilesByKey.containsKey(key) && tilesByKey[key]!!.tile.tileType != tileType
    }
    if(hasAllAsNeighbours)
      containsCount++
  }
  return containsCount == tileTypes.size
}

fun TileInstance.allNeighboursAre(tileType: String, tiles: Array<Array<TileInstance>>, offsetX : Int, offsetY:Int) : Boolean {
  var allAreOfType = true
  for(coord in MapService.neighbourMap.keys) {
    val x = this.x + coord.first - offsetX
    val y = this.y + coord.second - offsetY
    if(x < tiles.size - 1 && x > 0 && y < tiles[x].size - 1 && y > 0) {
      allAreOfType = allAreOfType && tiles[x][y].tile.tileType == tileType
    } else {
      allAreOfType = false
    }
  }
  return allAreOfType
}

fun TileInstance.atMostNAreOfType(tileType: String, n:Int, tiles: Array<Array<TileInstance>>, offsetX : Int, offsetY:Int) : Boolean {
  var count = 0
  for(coord in MapService.neighbourMap.keys) {
    val x = this.x + coord.first - offsetX
    val y = this.y + coord.second - offsetY
    if(x < tiles.size - 1 && x > 0 && y < tiles[x].size - 1 && y > 0) {
      if(tiles[x][y].tile.tileType == tileType)
        count++
    }
  }
  return count == n
}

fun TileInstance.isOfType(terrain: String) :Boolean  {
  return this.tile.tileType.equals(terrain)
}

fun Room.toggleBlink() {
  for(t in this.tileInstances) {
    t.blinking = !t.blinking
  }
}

fun List<TileInstance>.blink(blink: Boolean) {
  for(t in this) {
    t.blinking = blink
  }
}

fun Array<Array<TileInstance>>.toFlatArray() : Array<TileInstance> {
  if(this.isEmpty() || this.first().isEmpty()) return emptyArray()

  val columns = this.size
  val rows = this.first().size

  val size = rows * columns
  return Array(size) {
    val column = it.rem(columns)
    val row = it / columns
    return@Array this[column][row]
  }
}