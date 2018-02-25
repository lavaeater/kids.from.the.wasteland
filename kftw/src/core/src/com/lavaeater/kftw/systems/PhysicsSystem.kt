package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.lavaeater.kftw.components.Box2dBodyComponent
import com.lavaeater.kftw.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PhysicsSystem(private val world: World) : IteratingSystem(allOf(TransformComponent::class, Box2dBodyComponent::class).get()) {
  val bodyMpr = mapperFor<Box2dBodyComponent>()
  val transMpr = mapperFor<TransformComponent>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
    transMpr[entity]!!.position = bodyMpr[entity]!!.body.position
  }

  override fun update(deltaTime: Float) {
    val frameTime = Math.min(deltaTime, 0.25f)
    accumulator += frameTime
    if (accumulator >= MAX_STEP_TIME) {
      world.step(MAX_STEP_TIME, 6, 2)
      accumulator -= MAX_STEP_TIME
    }
    super.update(deltaTime)
  }

  companion object {
    private val MAX_STEP_TIME = 1 / 60f
    private var accumulator = 0f
  }
}

class PhysicsDebugSystem(private val world: World, private val camera: OrthographicCamera) : EntitySystem() {

  private val debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()

  override fun update(deltaTime: Float) {
    super.update(deltaTime)
    debugRenderer.render(world, camera.combined)
  }
}
