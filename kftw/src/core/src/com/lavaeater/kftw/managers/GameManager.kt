package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.systems.*
import com.lavaeater.kftw.ui.Hud
import ktx.ashley.add

class GameManager : Disposable {

  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val engine = Ctx.context.inject<Engine>()
  val actorManager = Ctx.context.inject<ActorFactory>()
  val messageDispatcher = Ctx.context.inject<MessageDispatcher>()
  val world = Ctx.context.inject<World>()
  val hud = Ctx.context.inject<Hud>()
  val gameStateManager = GameStateManager(::gameStateChanged)


  init {
    Ctx.context.register {
      bindSingleton(gameStateManager)
    }

    setupSystems()

    camera.position.x = 0f
    camera.position.y = 0f

    //Skip this while implementing monster spawn!
    //actorManager.addTownsFolk()
  }

  private fun setupSystems() {

    engine.addSystem(RenderMapSystem(true))
    engine.addSystem(RenderCharactersSystem())
    engine.addSystem(AiSystem())
    val npcControlSystem = NpcControlSystem()
    messageDispatcher.addListener(npcControlSystem, Messages.CollidedWithImpassibleTerrain)
    world.setContactListener(CollisionMessageManager())

    engine.addSystem(npcControlSystem)
    engine.addSystem(PhysicsSystem())
   //engine.addSystem(PhysicsDebugSystem())

    val playerEntity = actorManager.addHeroEntity()
    engine.addSystem(FollowCameraSystem(playerEntity))
    engine.addSystem(PlayerEntityDiscoverySystem(playerEntity))

    val inputSystem = KeyboardCharacterControlSystem()
    Gdx.input.inputProcessor = inputSystem
    engine.addSystem(inputSystem)

    //MONSTER SPAWN!!
    engine.addSystem(MonsterSpawningSystem())
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
  }

  fun gameStateChanged(newState: GameState) {
    when(newState){
      GameState.WorldMap -> resumeWorldMap()
      GameState.Inventory -> showInventory()
      else -> {
        //These aren't defined yet!
      }
    }
  }

  private fun showInventory() {
    stopTheWorld()

    hud.showInventory()

  }

  private fun stopTheWorld() {
    for (system in engine.systems.filter{ it !is RenderCharactersSystem && it !is RenderMapSystem })
      system.setProcessing(false)
  }

  private fun resumeWorldMap() {
    hud.hideInventory()
    resumeTheWorld()
  }

  private fun resumeTheWorld() {
    for (system in engine.systems)
      system.setProcessing(true)
  }
}