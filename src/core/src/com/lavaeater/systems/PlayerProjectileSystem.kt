package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.components.StateComponent
import com.lavaeater.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec3

/**
 * Created by tommie on 2017-06-29.
 */

class PlayerProjectileSystem(val createShot: (entity: Entity, vec3: Vector3, rotation: Float) -> Unit) : IteratingSystem(allOf(StateComponent::class).get()) {

    private val rateOfFire = 0.1f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        In the future, ships that fire gun must have ROF property for the weapons, here,
        we just use some constant value

        We need to, for now, store the "time elapsed since last shot" property in the statecomponent
        We might wanna move that to some other component later, but state will do for now

        When the time elapsed is larger than ROF-value, we set it to zero and add a pooled entity
        to the system
         */

        //1. get state component
        var stateComponent = scm.get(entity)
        if(!stateComponent.isFiring) return

        if(stateComponent.lastShotDelta > rateOfFire) {
            val transformComponent = tcm.get(entity)
            stateComponent.lastShotDelta = 0f
            createShot(entity, vec3(transformComponent.position.x, transformComponent.position.y, transformComponent.position.z), transformComponent.rotation)
            Assets.pewSound.play()
        }
        else {
            stateComponent.lastShotDelta += deltaTime
        }
    }

    private var tcm = mapperFor<TransformComponent>()
    private var scm = mapperFor<StateComponent>()
}

