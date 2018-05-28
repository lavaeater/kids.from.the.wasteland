package managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.lavaeater.Assets
import com.lavaeater.kftw.GameSettings
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.*
import com.lavaeater.kftw.systems.*
import com.lavaeater.kftw.ui.IUserInterface
import map.IMapManager
import world.*

class GameManager(
    gameSettings: GameSettings,
    gameState: GameState,
    private val batch: Batch,
    private val camera: Camera,
    viewPortProvider: () -> Viewport,
    private val engine: Engine, //Engine will come with all systems added already, yay!
    actorFactoryProvider: () -> ActorFactory,
    private val messageDispatcher: MessageDispatcher,
    private val world: World,
    private val ui: IUserInterface,
    private val mapManager: IMapManager) : Disposable {

  private val viewPort = viewPortProvider()
  private val actorFactory = actorFactoryProvider()

  init {
    gameState.addChangeListener(::gameStateChanged)
    setupSystems()

    setupRules()
    setupFacts()
    VIEWPORT_WIDTH = gameSettings.width
    VIEWPORT_HEIGHT = gameSettings.height
    TILE_SIZE = gameSettings.tileSize

    camera.position.x = 0f
    camera.position.y = 0f

    Assets.music.play()
  }

  private fun setupFacts() {
    FactsOfTheWorld.setupInitialFacts()
  }

  private fun setupRules() {
    RulesOfTheWorld.setupRules()
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

    val playerEntity = actorFactory.addHeroEntity()
    engine.addSystem(FollowCameraSystem(playerEntity))
    //engine.addSystem(PlayerEntityDiscoverySystem(playerEntity))

//    engine.addSystem(charControlSystemProvider())

    addBeamonPeople()
    //MONSTER SPAWN!!
    //engine.addSystem(MonsterSpawningSystem(false))

    //Current tile system. Continually updates the agent instances with
    //what tile they're on, used by the AI
    engine.addSystem(WorldFactsSystem())
  }

  private fun addBeamonPeople() {
    for (name in FactsOfTheWorld.npcNames.values) {
      val someTilesInRange = mapManager.getBandOfTiles(0,0, 2, 3).filter {
        it.tile.tileType != "rock" && it.tile.tileType != "water"
      }

      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
      actorFactory.addNpcAtTileWithAnimation(name = name,type = "orc", x = randomlySelectedTile.x, y = randomlySelectedTile.y)
    }
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

  private fun gameStateChanged(newState: GameStates) {
    when(newState){
      GameStates.WorldMap -> resumeWorldMap()
      GameStates.Inventory -> showInventory()
      GameStates.Dialog -> showDialog()
      else -> {
        //These aren't defined yet!
      }
    }
  }

  private fun showInventory() {
    stopTheWorld()
    ui.showInventory()
  }

  private fun showDialog() {
    stopTheWorld()
  }

  private fun stopTheWorld() {
    for (system in engine.systems.filter { it !is RenderCharactersSystem && it !is RenderMapSystem }) {
      system.setProcessing(false)
      if (system is CharacterControlSystem) {
        system.processInput = false
      }

    }
  }

  private fun resumeWorldMap() {
    ui.hideInventory()
    resumeTheWorld()
  }

  private fun resumeTheWorld() {
    for (system in engine.systems) {
      system.setProcessing(true)
      if(system is CharacterControlSystem)
      {
        system.processInput = true
      }
    }
  }
}