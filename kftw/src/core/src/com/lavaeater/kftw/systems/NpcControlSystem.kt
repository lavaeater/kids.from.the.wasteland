package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.kftw.components.Npc
import com.lavaeater.kftw.components.NpcComponent
import com.lavaeater.kftw.components.NpcState
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.map.TileKey
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class NpcControlSystem : IteratingSystem(allOf(NpcComponent::class, TransformComponent::class).get()) {

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
  }

  private fun walkToTile(foundTile: TileKey, transform: TransformComponent) {
    val desiredPos = foundTile.tileWorldCenter
  }

  private fun comeWalkWithMe(npc: Npc, transform: TransformComponent) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}