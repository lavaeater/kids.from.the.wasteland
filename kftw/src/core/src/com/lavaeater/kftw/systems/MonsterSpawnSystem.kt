package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.lavaeater.kftw.components.PlayerComponent
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.map.IMapManager
import ktx.ashley.allOf

class MonsterSpawnSystem : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), 10f) {
  val actorFactory = Ctx.context.inject<ActorFactory>()
  val mapManager = Ctx.context.inject<IMapManager>()

  override fun processEntity(entity: Entity?) {
    /*
    For every type of tile, for every 10 seconds, there is some chance of a creature being spawned.

    The creature will have some sort of behavior tree and it will perhaps be dangerous.

    Some creatures can use stealth, etc.

    The players sight radius is not unlimited.

    This system only deals with spawning random monsters that might show up basically anywhere as
    autonomous beings that run around.

    There should be some kind of cap on the amount of monsters at the same time

     */
  }
}