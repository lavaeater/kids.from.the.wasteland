package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
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

class NpcControlSystem : IteratingSystem(allOf(NpcComponent::class, TransformComponent::class).get(), 10) {

  val npcMapper = mapperFor<NpcComponent>()
  val transformMapper = mapperFor<TransformComponent>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
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
    val desiredPos = foundTile.tileWorldCenter(GameManager.TILE_SIZE)
    transform.x = MathUtils.lerp(transform.x, desiredPos.x, 0.01f) //Take characters speed into consideration
    transform.y = MathUtils.lerp(transform.y, desiredPos.y, 0.01f)
  }

  private fun comeWalkWithMe(npc: Npc, transform: TransformComponent) {
    //The Npc manages its own state, preferrably?
    val desiredPos = npc.wanderTarget.tileWorldCenter(GameManager.TILE_SIZE)
    transform.x = MathUtils.lerp(transform.x, desiredPos.x, 0.01f) //Take characters speed into consideration
    transform.y = MathUtils.lerp(transform.y, desiredPos.y, 0.01f)
  }
}