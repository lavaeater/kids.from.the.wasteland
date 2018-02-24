package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
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
import ktx.math.*

class NpcControlSystem : IteratingSystem(allOf(NpcComponent::class, TransformComponent::class).get(),10) {
  val npcMapper = mapperFor<NpcComponent>()
  val transformMapper = mapperFor<TransformComponent>()

  override fun processEntity(entity: Entity, deltaTime:Float) {
    val npc = npcMapper[entity].npc
    val transform = transformMapper[entity]!!

    when(npc.state) {
      NpcState.Idle -> return
      NpcState.Wandering -> comeWalkWithMe(npc, transform, deltaTime)
      NpcState.WalkingTo -> if(npc.tileFound) walkToTile(npc.foundTile!!, transform, deltaTime)
      NpcState.Scavenging -> return //Replace with some animation or some other stuff
      NpcState.Searching -> return //This code doesn't need to do anything for this state, maybe anim later?
    }

    val currentPos = transform.position.toTile(GameManager.TILE_SIZE)
    npc.currentTile = currentPos
  }

  private fun walkToTile(foundTile: TileKey, transform: TransformComponent, deltaTime: Float) {
      moveFromTo(transform, foundTile.tileWorldCenter(GameManager.TILE_SIZE), deltaTime)
  }

  private fun comeWalkWithMe(npc: Npc, transform: TransformComponent, deltaTime: Float) {
    //The Npc manages its own state, preferrably?
    moveFromTo(transform, npc.wanderTarget.tileWorldCenter(GameManager.TILE_SIZE), deltaTime)
  }

  private fun moveFromTo(transform: TransformComponent, desiredPos: Vector2, deltaTime: Float) {
    val vel = (desiredPos - transform.position).nor() * 5f

    transform.position += vel * deltaTime
  }
}

fun Vector2.directionalVelocity(velocity : Float) : Vector2 {
  return (vec2(0f,0f) - this).nor() * velocity
}