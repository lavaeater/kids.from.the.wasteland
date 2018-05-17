package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.GameSettings
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.systems.*
import com.lavaeater.kftw.ui.IUserInterface
import managers.MessageManager

class GameManager(private val gameSettings: GameSettings) : Disposable {
  val batch = Ctx.context.inject<Batch>()
  val camera = Ctx.context.inject<Camera>()
  val viewPort = ExtendViewport(gameSettings.width, gameSettings.height, camera)
  val engine = Ctx.context.inject<Engine>()
  val actorManager = Ctx.context.inject<ActorFactory>()
  val messageDispatcher = Ctx.context.inject<MessageDispatcher>()
  val world = Ctx.context.inject<World>()
  val hud = Ctx.context.inject<IUserInterface>()


  init {
    Ctx.context.inject<GameStateManager>().apply { addChangeListener(::gameStateChanged) }
    setupSystems()
    VIEWPORT_WIDTH = gameSettings.width
    VIEWPORT_HEIGHT = gameSettings.height
    TILE_SIZE = gameSettings.tileSize

    camera.position.x = 0f
    camera.position.y = 0f

    //Skip this while implementing monster spawn!
    //actorManager.addTownsFolk()
  }

  private fun setupSystems() {

    //render the map and use fog of war
    engine.addSystem(RenderMapSystem(false))
    engine.addSystem(RenderCharactersSystem())
    engine.addSystem(AiSystem())
    val npcControlSystem = NpcControlSystem()

    setupMessageSystem()

    world.setContactListener(CollisionManager())

    engine.addSystem(npcControlSystem)
    engine.addSystem(PhysicsSystem())
   //engine.addSystem(PhysicsDebugSystem())

    val playerEntity = actorManager.addHeroEntity()
    engine.addSystem(FollowCameraSystem(playerEntity))
    engine.addSystem(PlayerEntityDiscoverySystem(playerEntity))

    val inputSystem = CharacterControlSystem()
    Gdx.input.inputProcessor = inputSystem
    engine.addSystem(inputSystem)

    //MONSTER SPAWN!!
    engine.addSystem(MonsterSpawningSystem(false))

    //Current tile system. Continually updates the agent instances with
    //what tile they're on, used by the AI
    engine.addSystem(CurrentTileSystem())
  }

  private fun setupMessageSystem() {
    val messageManager = Ctx.context.inject<MessageManager>()
    messageDispatcher.addListener(messageManager, Messages.CollidedWithImpassibleTerrain)
    messageDispatcher.addListener(messageManager, Messages.PlayerMetSomeone)
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
    var VIEWPORT_HEIGHT = 64f
    var VIEWPORT_WIDTH = 48f
    var TILE_SIZE = 8
  }

  fun pause() {
  }

  fun gameStateChanged(newState: GameState) {
    when(newState){
      GameState.WorldMap -> resumeWorldMap()
      GameState.Inventory -> showInventory()
      GameState.Dialog -> showDialog()
      else -> {
        //These aren't defined yet!
      }
    }
  }

  private fun showInventory() {
    stopTheWorld()
    hud.showInventory()
  }

  private fun showDialog() {
    stopTheWorld()
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