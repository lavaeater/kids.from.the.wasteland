package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.screens.Ctx
import com.lavaeater.kftw.systems.*

class GameStateMachine {

}

class GameManager : Disposable {

  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val engine = Ctx.context.inject<Engine>()
  val actorManager = Ctx.context.inject<ActorManager>()
  val messageDispatcher = Ctx.context.inject<MessageDispatcher>()
  val world = Ctx.context.inject<World>()

  init {
    setupSystems()

    camera.position.x = 0f
    camera.position.y = 0f

    actorManager.addTownsFolk()
  }

  private fun setupSystems() {

    engine.addSystem(RenderMapSystem())
    engine.addSystem(RenderCharactersSystem())
    engine.addSystem(AiSystem())
    val npcControlSystem = NpcControlSystem()
    messageDispatcher.addListener(npcControlSystem, Messages.CollidedWithImpassibleTerrain)
    world.setContactListener(CollisionMessageManager())

    engine.addSystem(npcControlSystem)
    engine.addSystem(PhysicsSystem())
    //engine.addSystem(PhysicsDebugSystem(world, camera))

    engine.addSystem(FollowCameraSystem(actorManager.addHeroEntity()))
    val inputSystem = KeyboardCharacterControlSystem()
    Gdx.input.inputProcessor = inputSystem
    engine.addSystem(inputSystem)
  }

  fun update(delta: Float) {
    engine.update(delta)
    messageDispatcher.update()
  }

  fun resize(width: Int, height: Int) {
    viewPort.update(width, height)
    batch.projectionMatrix = camera.combined
  }

  override fun dispose() {
    batch.dispose()
  }

  companion object {
    val VIEWPORT_HEIGHT = 128f
    val VIEWPORT_WIDTH = 96f
    val TILE_SIZE = 8
  }

  fun pause() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}