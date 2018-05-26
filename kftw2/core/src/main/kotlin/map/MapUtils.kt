package map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.injection.Ctx
import managers.GameManager
import com.lavaeater.kftw.map.Tile
import com.lavaeater.kftw.map.TileInstance
import com.lavaeater.kftw.util.SimplexNoise
import ktx.math.vec3
import kotlin.math.absoluteValue

fun String.toShortCode() : String {
  return "${this[0]}${this[1]}${this[3]}${this[5]}${this[7]}"
}

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

fun Int.getMinMax(range:Int) : Pair<Int, Int> {
    return Pair(this - range, this + range)
}

fun getTilePriorityFromNoise(x: Float, y: Float, tileX:Int, tileY:Int): Int {

  val distanceFactor = Math.min((tileX.absoluteValue + tileY.absoluteValue) / (40.0), 1.0)

  val part1 = distanceFactor * -100
  val part2 = (1 - distanceFactor) * (getNoiseNotAbs(x, y, 1.0, 0.5, 0.25) * 100)

  val noiseValue = part1 + part2
  Gdx.app.log("testTag","$x, $y, ${noiseValue} $distanceFactor")

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

fun Tile.isImpassible() : Boolean {
  return (this.priority == 0 || this.priority == 3) && !this.shortCode.isOneTerrain()
}

fun Tile.getSprite() : Sprite {
  return Assets.sprites[this.tileType]!![this.subType]!!
}

fun Tile.getExtraSprites() : Array<Sprite> {
  if (Assets.codeToExtraTiles.containsKey(this.shortCode))
    return Assets.codeToExtraTiles[this.shortCode]!!.toTypedArray()
  return emptyArray()
}

//fun MutableMap<TileKey, Int>.getTileKeyForDirection(key: TileKey, directionKey: TileKey): TileKey {
//  return Ctx.context.inject<TileKeyManager>().tileKey(key.x + directionKey.x, key.y + directionKey.y)
//}