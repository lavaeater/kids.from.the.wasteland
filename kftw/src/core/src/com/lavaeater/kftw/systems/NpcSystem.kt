package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.kftw.components.NpcComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class NpcSystem : IteratingSystem(allOf(NpcComponent::class).get()) {
    val mapper = mapperFor<NpcComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        var npcComponent = mapper[entity]

    }

}