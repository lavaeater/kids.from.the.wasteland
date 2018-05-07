package com.lavaeater.kftw.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.MapManager
import com.lavaeater.kftw.map.TileInstance

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
          override var currentX: Int = 0,
          override var currentY: Int = 0) : IAgent {
  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "grass"
  var tileFound = false
  var foundX:Int = 0
  var foundY:Int = 0
  val range = 2

  val mapManager = Ctx.context.inject<IMapManager>()

  fun log(message: String) {
    brainLog += "$name: $message\n"
    Gdx.app.log(name, message)
  }

  fun lostInterest() {
    state = NpcState.Searching
    val terrainArr = MapManager.terrains.filterValues { it != desiredTileType && it != "rock" && it != "water" }.values.toTypedArray()
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

    if (mapManager.getTileAt(currentX,currentY).tileType == desiredTileType) {
      state = NpcState.Scavenging
      log("Jag letar mat vid ${currentX}:${currentY} nu.")
      return true
    }
    return false
  }

  var targetTile: TileInstance? = null

  fun wander(): Boolean {
    if (state == NpcState.Wandering && currentX == foundX && currentY == foundY) {
      state = NpcState.Idle
      return false
    }

    if (state == NpcState.Idle || state == NpcState.Scavenging)
      return false //I am NOT doing this right, I realize. I have to read more

    if (state != NpcState.Wandering) {
      val possibleTargets = mapManager.getRingOfTiles(currentX, currentY, 5).toTypedArray()
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

    if (currentX == foundX && currentY == foundY) {
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
    targetTile =  mapManager.findTileOfTypeInRange(currentX, currentY, desiredTileType, range)
    tileFound = targetTile != null

    return targetTile != null
  }
}