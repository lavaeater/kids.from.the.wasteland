package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.lavaeater.kftw.components.AgentComponent
import com.lavaeater.kftw.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class CurrentTileSystem : IntervalIteratingSystem(allOf(TransformComponent::class, AgentComponent::class).get(),0.5f) {
  val transMpr = mapperFor<TransformComponent>()
  val agentMpr = mapperFor<AgentComponent>()

  override fun processEntity(entity: Entity) {
    agentMpr[entity].agent.currentTile = transMpr[entity].position.toTile()
  }
}