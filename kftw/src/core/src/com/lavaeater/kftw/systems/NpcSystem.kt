package com.lavaeater.kftw.systems

import com.badlogic.gdx.ai
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.math.Vector2
import com.lavaeater.kftw.components.NpcComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor



class SteeringSystem : IteratingSystem(allOf(NpcComponent::class).get()), Steerable<Vector2> {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


class NpcSystem : IteratingSystem(allOf(NpcComponent::class).get()) {
    val mapper = mapperFor<NpcComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        var npcComponent = mapper[entity]

    }
}



class AiDef {
    val def = "root" +
            "   selector" +
            "       "
}