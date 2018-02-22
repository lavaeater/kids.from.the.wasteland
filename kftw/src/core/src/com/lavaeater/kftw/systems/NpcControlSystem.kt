package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.lavaeater.kftw.components.Npc
import com.lavaeater.kftw.components.NpcComponent
import com.lavaeater.kftw.components.NpcState
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.TileKey
import com.lavaeater.kftw.map.tileWorldCenter
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math

class NpcControlSystem : IntervalIteratingSystem(allOf(NpcComponent::class, TransformComponent::class).get(), 0.1f, 10) {
  val npcMapper = mapperFor<NpcComponent>()
  val transformMapper = mapperFor<TransformComponent>()

  override fun processEntity(entity: Entity) {
    val npc = npcMapper[entity].npc
    val transform = transformMapper[entity]!!

    when(npc.state) {
      NpcState.Idle -> return
      NpcState.Wandering -> comeWalkWithMe(npc, transform)
      NpcState.WalkingTo -> if(npc.tileFound) walkToTile(npc.foundTile!!, transform)
      NpcState.Scavenging -> return //Replace with some animation or some other stuff
      NpcState.Searching -> return //This code doesn't need to do anything for this state, maybe anim later?
    }

    val currentPos = Vector2(transform.x, transform.y).toTile(GameManager.TILE_SIZE)
    npc.currentTile = currentPos
  }

  private fun walkToTile(foundTile: TileKey, transform: TransformComponent) {
      moveFromTo(transform, foundTile.tileWorldCenter(GameManager.TILE_SIZE))
  }

  private fun comeWalkWithMe(npc: Npc, transform: TransformComponent) {
    //The Npc manages its own state, preferrably?
    moveFromTo(transform, npc.wanderTarget.tileWorldCenter(GameManager.TILE_SIZE))
  }

  private fun moveFromTo(transform: TransformComponent, desiredPos: Vector2) {
//    val vel = desiredPos - transform.position

//    transform.x += transform.x - desiredPos.x + 0.05f //Take characters speed into consideration
//    transform.y += transform.y - desiredPos.y + 0.05f
  }
}