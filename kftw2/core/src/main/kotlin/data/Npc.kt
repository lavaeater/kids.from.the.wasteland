package com.lavaeater.kftw.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.MapManagerBase
import com.lavaeater.kftw.map.TileKey
import map.TileKeyStore

class Npc(override var name:String ="Joshua",
          val npcType: NpcType,
          override var strength: Int = npcType.strength,
          override var health: Int = npcType.health,
          var speed: Int = npcType.speed,
          var attack: Int = npcType.attack,
          var attackString: String = npcType.attackString,
          override val skills: MutableMap<String, Int> = npcType.skills.toMutableMap(),
          override var intelligence: Int = npcType.intelligence,
          override val inventory: MutableList<String> = npcType.inventory,
          override var sightRange: Int = npcType.sightRange,
          override var currentTile: TileKey = Ctx.context.inject<TileKeyStore>().tileKey(0,0)) : IAgent {
  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "grass"
  var foundTile: TileKey? = null
  val tileFound get() = foundTile != null
  val range = 2

  val mapManager = Ctx.context.inject<IMapManager>()
  val tileKeyStore = Ctx.context.inject<TileKeyStore>()

  fun log(message: String) {
    brainLog += "$name: $message\n"
    Gdx.app.log(name, message)
  }

  fun lostInterest() {
    state = NpcState.Searching
    val terrainArr = MapManagerBase.terrains.filterValues { it != desiredTileType && it != "rock" && it != "water" }.values.toTypedArray()
    val randomIndex = MathUtils.random(0, terrainArr.size - 1)
    desiredTileType = terrainArr[randomIndex]
    foundTile = null
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

    if (mapManager.getTileAt(currentTile).tileType == desiredTileType) {
      state = NpcState.Scavenging
      log("Jag letar mat vid $currentTile nu.")
      return true
    }
    return false
  }

  fun wander(): Boolean {
    if (state == NpcState.Wandering && wanderTarget == currentTile) {
      state = NpcState.Idle
      return false
    }

    if (state == NpcState.Idle || state == NpcState.Scavenging)
      return false //I am NOT doing this right, I realize. I have to read more

    if (state != NpcState.Wandering) {
      val possibleTargets = mapManager.getRingOfTiles(currentTile, 5).toTypedArray()
      if(!possibleTargets.any()) return false
      wanderTarget = possibleTargets[MathUtils.random(0, possibleTargets.size - 1)]
      log("Jag hittar inte ${translate(desiredTileType)}, jag går till $wanderTarget och letar.")
      state = NpcState.Wandering
    }

    return true
  }

  fun walkTo(): Boolean {
    if (state != NpcState.WalkingTo)
      state = NpcState.WalkingTo

    if (currentTile == foundTile) {
      log("Jag är framme vid $currentTile nu.")
      foundTile = null
      state = NpcState.Idle //Need more states?
      return false //This returning false means we are at our destination!
    }
    return true
  }

  fun findTile(): Boolean {
    if (state == NpcState.WalkingTo) return true

    log("Jag försöker hitta $desiredTileType nu!")
    state = NpcState.Searching
    foundTile =  mapManager.findTileOfTypeInRange(currentTile, desiredTileType, range)
    return foundTile != null
  }

  var wanderTarget: TileKey = tileKeyStore.tileKey(0,0)
}