package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IntervalSystem
import com.lavaeater.kftw.components.AgentComponent
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.data.Player
import injection.Ctx
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class WorldFactsSystem : IntervalIteratingSystem(allOf(TransformComponent::class, AgentComponent::class).get(),0.5f) {
  val player = Ctx.context.inject<Player>()
  val transMpr = mapperFor<TransformComponent>()
  val agentMpr = mapperFor<AgentComponent>()

  override fun processEntity(entity: Entity) {
    agentMpr[entity].agent.apply {
      currentX = transMpr[entity].position.tileX()
      currentY = transMpr[entity].position.tileY()
    }
  }
}

class GlobalWorldFactSystem : IntervalSystem(5f) {

  override fun updateInterval() {

  }

}