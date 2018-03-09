package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.kftw.components.NpcComponent
import com.lavaeater.kftw.components.PlayerComponent
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.components.VisibleComponent
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.map.IMapManager
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerEntityDiscoverySystem(val playerEntity: Entity) :
    IteratingSystem(allOf(TransformComponent::class, NpcComponent::class).get(),1) {

  val transMpr = mapperFor<TransformComponent>()
  val npcMpr = mapperFor<NpcComponent>()
  val visibilityMapper = mapperFor<VisibleComponent>()

  val player = mapperFor<PlayerComponent>()[playerEntity]!!

  val mapManager = Ctx.context.inject<IMapManager>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
    val discoverySkillForPlayer
  }

}