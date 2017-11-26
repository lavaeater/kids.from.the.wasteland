package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

/**
 * Created by barry on 12/8/15 @ 10:11 PM.
 */
class PhysicsSystem(private val world: World) : IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get(), 1) {
    val bodyMapper = mapperFor<BodyComponent>()
    val transformMapper = mapperFor<TransformComponent>()
    val entityQueue = mutableListOf<Entity>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(!entityQueue.contains(entity)) entityQueue.add(entity)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2)
            accumulator -= MAX_STEP_TIME
            processEntities()
        }
    }

    private fun processEntities() {
        for(entity in entityQueue) {
            val bodyComponent = bodyMapper.get(entity)
            val transformComponent = transformMapper.get(entity)
            val position = bodyComponent.body.position//.sub(bodyComponent.origin)

            transformComponent.position.set(position.x, position.y, 0f)
            transformComponent.rotation = bodyComponent.body.angle * MathUtils.radiansToDegrees
        }
        entityQueue.clear()
    }

    companion object {
        private val MAX_STEP_TIME = 1 / 60f
        private var accumulator = 0f
    }
}
