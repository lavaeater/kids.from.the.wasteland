package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.ProjectileComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class ProjectileMovementSystem: IteratingSystem(allOf(ProjectileComponent::class).get()) {
    val pcm = mapperFor<ProjectileComponent>()
    val bcm = mapperFor<BodyComponent>()
    override fun processEntity(entity: Entity?, deltaTime: Float) {
//        val bc = bcm.get(entity)
//        val body = bc.body
//
//        val rotation = body.angle - MathUtils.PI * 0.5f
//        val thrustForce = 10000f
//        val forceVector = Vector2(MathUtils.cos(rotation), MathUtils.sin(rotation)).nor().scl(thrustForce)
//
//        body.applyForce(forceVector, body.position, true)
    }

}