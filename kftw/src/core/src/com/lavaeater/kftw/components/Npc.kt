package com.lavaeater.kftw.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.MapManagerBase
import com.lavaeater.kftw.map.TileKey

class Npc(val name:String ="Joshua", val npcType: NpcType, var strength: Int = npcType.strength, var health: Int = npcType.health, var speed: Int = npcType.speed, var attack: Int = npcType.attack, var attackString: String = npcType.attackString) {
  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "rock"
  var currentTile = TileKey(0, 0)
  var foundTile: TileKey? = null
  val tileFound get() = foundTile != null
  val range = 2

  fun log(message: String) {
    Gdx.app.log(name, message)
  }

  fun lostInterest() {
    state = NpcState.Searching
    val terrainArr = MapManagerBase.terrains.filterValues { it != desiredTileType }.values.toTypedArray()
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

    if (GameManager.MapManager.getTileAt(currentTile).tileType == desiredTileType) {
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
      val possibleTargets = GameManager.MapManager.getRingOfTiles(currentTile, 5).toTypedArray()
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
    foundTile = GameManager.MapManager.findTileOfType(currentTile, desiredTileType, range)
    return foundTile != null
  }

  var wanderTarget: TileKey = TileKey(0, 0)
}

enum class NpcState {
  Idle,
  Searching,
  Scavenging,
  Wandering,
  WalkingTo
}