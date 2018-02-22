package com.lavaeater.kftw.components

import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.MapManagerBase
import com.lavaeater.kftw.map.TileKey

class Npc(val npcType: NpcType, var strength: Int = npcType.strength, var health: Int = npcType.health, var speed: Int = npcType.speed, var attack: Int = npcType.attack, var attackString: String = npcType.attackString) {
  var brainLog = ""
  var state = NpcState.Idle
  var desiredTileType = "rock"
  var currentTile = TileKey(0,0)
  var foundTile: TileKey? = null
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
    if (state == NpcState.Scavenging) return true // already scavening, early exit

    if (GameManager.MapManager.getTileAt(currentTile).tileType == desiredTileType) {
      state = NpcState.Scavenging
      log("I am now scavenging at $foundTile!!")
      return true
    }
    return false
  }

  fun wander() : Boolean {
    if(state == NpcState.Idle) //Idle means we're at the tile we're going for!
      return false
    if(state != NpcState.Idle && state != NpcState.Wandering) {
      val possibleTargets = GameManager.MapManager.getRingOfTiles(currentTile, 5).toTypedArray()
      wanderTarget = possibleTargets[MathUtils.random(0, possibleTargets.size -1)]
      state = NpcState.Wandering
    }
    if(state == NpcState.Wandering) {
        if(wanderTarget == currentTile)
            return false
    }
    return true
  }

  fun walkTo(): Boolean {
    if (!tileFound) return false
    if (state != NpcState.WalkingTo)
      state = NpcState.WalkingTo
    return true
  }

  fun findTile(): Boolean {
    state = NpcState.Searching
    foundTile = GameManager.MapManager.findTileOfType(currentTile, desiredTileType, range)
    return foundTile != null
  }

  var wanderTarget: TileKey = TileKey(0,0)
}

enum class NpcState {
  Idle,
  Searching,
  Scavenging,
  Wandering,
  WalkingTo
}