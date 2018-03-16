package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.lavaeater.kftw.components.AiComponent
import com.lavaeater.kftw.data.Npc
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class AiSystem : IntervalIteratingSystem(allOf(AiComponent::class).get(), 5f, 5) {
  val mapper = mapperFor<AiComponent<Npc>>()

  override fun processEntity(entity: Entity?) {
    val aiComponent = mapper[entity]

    //And now we step the decision tree!
    aiComponent.behaviorTree.step()
  }
}