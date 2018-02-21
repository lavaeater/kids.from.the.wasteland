package com.lavaeater.kftw.components

import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.MapManagerBase
import com.lavaeater.kftw.map.TileKey

class Npc(val npcType: NpcType, var strength: Int = npcType.strength, var health: Int = npcType.health, var speed: Int = npcType.speed, var attack: Int = npcType.attack, var attackString: String = npcType.attackString) {
  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "rock"
  var x: Int = 0
  var y: Int = 0
  var foundTile : TileKey? = null
  val tileFound get() = foundTile != null
  val range = 2

  fun log(message: String) {
    brainLog += message + "\n"
  }

  fun lostInterest() {
    state = NpcState.Searching
    val terrainArr = MapManagerBase.terrains.filterValues { it != desiredTileType }.values.toTypedArray()
    val randomIndex = MathUtils.random(0, terrainArr.size - 1)
    desiredTileType = terrainArr[randomIndex]
    foundTile = null
    log("Now I want to find $desiredTileType")
  }

  fun scavenge(): Boolean {
    //A little goeey, but what's the best way?
    if (GameManager.MapManager.getTileAt(x, y).tileType == desiredTileType) {
      state = NpcState.Scavenging
      return true
    }
    return false
  }

  fun wander() {
    state = NpcState.Wandering
  }

  fun walkTo(): Boolean {
    if (!tileFound) return false
    if (state != NpcState.WalkingTo)
      state = NpcState.WalkingTo
    return true
  }

  fun findTile(): Boolean {
    foundTile = GameManager.MapManager.findTileOfType(x, y, desiredTileType, range)
    return foundTile != null
  }
}

enum class NpcState {
  Idle,
  Searching,
  Scavenging,
  Wandering,
  WalkingTo
}