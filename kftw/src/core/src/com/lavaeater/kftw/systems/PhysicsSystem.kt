package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class PhysicsSystem(private val world: World) : EntitySystem() {

  override fun update(deltaTime: Float) {
    val frameTime = Math.min(deltaTime, 0.25f)
    accumulator += frameTime
    if (accumulator >= MAX_STEP_TIME) {
      world.step(MAX_STEP_TIME, 6, 2)
      accumulator -= MAX_STEP_TIME
    }
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
