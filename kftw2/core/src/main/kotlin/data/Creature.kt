package data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import injection.Ctx
import map.ILocationManager
import map.LocationManager
import map.TileInstance
import systems.toTile

class EmptyAgent(
    override val id: String = "Place",
    override var name: String = "PLace",
    override var strength: Int =0,
    override var health: Int = 0,
    override var intelligence: Int = 0,
    override var sightRange: Int = 0,
    override val inventory: MutableList<String> = mutableListOf(),
    override val skills: MutableMap<String, Int> = mutableMapOf(),
    override var tileX: Int = 0,
    override var tileY: Int = 0,
    override var worldX: Float = 0f,
    override var worldY: Float = 0f,
    override val tileKey: Pair<Int, Int> = Pair(0,0),
    override var speed: Int = 10,
    override var attack: Int = 10,
    override var attackString: String = "slams"
) :IAgent {
}

class Creature(override val id: String,
               override var name: String = "Joshua",
               val creatureType: CreatureType,
               override var strength: Int = creatureType.strength,
               override var health: Int = creatureType.health,
               override var speed: Int = creatureType.speed,
               override var attack: Int = creatureType.attack,
               override var attackString: String = creatureType.attackString,
               override val skills: MutableMap<String, Int> = creatureType.skills.toMutableMap(),
               override var intelligence: Int = creatureType.intelligence,
               override val inventory: MutableList<String> = creatureType.inventory,
               override var sightRange: Int = creatureType.sightRange,
               override var worldX: Float = 0f,
               override var worldY: Float = 0f) : IAgent {
  override var tileX: Int
    get() = worldX.toTile()
    set(value) {}
  override var tileY: Int
    get() = worldY.toTile()
    set(value) {}
  override val tileKey: Pair<Int, Int>
    get() = Pair(tileX, tileY)

  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "grass"
  var tileFound = false
  var foundX:Int = 0
  var foundY:Int = 0
  val range = 2

  val mapManager = Ctx.context.inject<ILocationManager>()

  fun log(message: String) {
    brainLog += "$name: $message\n"
    Gdx.app.log(name, message)
  }

  fun lostInterest() {
    state = NpcState.Searching
    val terrainArr = LocationManager.terrains.filterValues { it != desiredTileType && it != "rock" && it != "water" }.values.toTypedArray()
    val randomIndex = MathUtils.random(0, terrainArr.size - 1)
    desiredTileType = terrainArr[randomIndex]
    tileFound = false
    log("Att leta mat är tråkigt, nu vill jag hitta ${translate(desiredTileType)}")
  }

  val translations = mapOf("grass" to "gräs",
      "rock" to "sten",
      "water" to "vatten",
      "desert" to "öken")
  private fun translate(word: String): String {
    return translations[word]!!
  }

  fun scavenge(): Boolean {
    //A little goeey, but what's the best way?
    if (state == NpcState.Scavenging) return true // already scavening, early exit

    if (mapManager.getTileAt(tileX,tileY).tileType == desiredTileType) {
      state = NpcState.Scavenging
      log("Jag letar mat vid ${tileX}:${tileY} nu.")
      return true
    }
    return false
  }

  var targetTile: TileInstance? = null

  fun wander(): Boolean {
    if (state == NpcState.Wandering && tileX == foundX && tileY == foundY) {
      state = NpcState.Idle
      return false
    }

    if (state == NpcState.Idle || state == NpcState.Scavenging)
      return false //I am NOT doing this right, I realize. I have to read more

    if (state != NpcState.Wandering) {
      val possibleTargets = mapManager.getRingOfTiles(tileX, tileY, 5).toTypedArray()
      if(!possibleTargets.any()) return false
      targetTile = possibleTargets[MathUtils.random(0, possibleTargets.size - 1)]
      targetTile?.let {
        foundX = it.x
        foundY = it.y
      }

      log("Jag hittar inte ${translate(desiredTileType)}, jag går till $targetTile och letar.")
      state = NpcState.Wandering
    }

    return true
  }

  fun walkTo(): Boolean {
    if (state != NpcState.WalkingTo)
      state = NpcState.WalkingTo

    if (tileX == foundX && tileY == foundY) {
      log("Jag är framme vid ${foundX}${foundY} nu.")
      tileFound = false
      state = NpcState.Idle //Need more states?
      return false //This returning false means we are at our destination!
    }
    return true
  }

  fun findTile(): Boolean {
    if (state == NpcState.WalkingTo) return true

    log("Jag försöker hitta $desiredTileType nu!")
    state = NpcState.Searching
    targetTile =  mapManager.findTileOfTypeInRange(tileX, tileY, desiredTileType, range)
    tileFound = targetTile != null

    return targetTile != null
  }
}