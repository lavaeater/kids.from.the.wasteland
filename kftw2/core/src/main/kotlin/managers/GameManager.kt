package managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.GameSettings
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.*
import com.lavaeater.kftw.systems.*
import com.lavaeater.kftw.ui.IUserInterface
import map.IMapManager
import world.*

class GameManager(gameSettings: GameSettings) : Disposable {
  val batch = Ctx.context.inject<Batch>()
  val camera = Ctx.context.inject<Camera>()
  val viewPort = ExtendViewport(gameSettings.width, gameSettings.height, camera)
  val engine = Ctx.context.inject<Engine>()
  val actorFactory = Ctx.context.inject<ActorFactory>()
  val messageDispatcher = Ctx.context.inject<MessageDispatcher>()
  val world = Ctx.context.inject<World>()
  val hud = Ctx.context.inject<IUserInterface>()
  val mapManager = Ctx.context.inject<IMapManager>()


  init {
    Ctx.context.inject<GameStateManager>().apply { addChangeListener(::gameStateChanged) }
    setupSystems()

    setupRules()
    setupFacts()
    VIEWPORT_WIDTH = gameSettings.width
    VIEWPORT_HEIGHT = gameSettings.height
    TILE_SIZE = gameSettings.tileSize

    camera.position.x = 0f
    camera.position.y = 0f

    //Skip this while implementing monster spawn!
    //actorFactory.addTownsFolk()
  }

  private fun setupFacts() {
    FactsOfTheWorld.stateIntFact("MetNumberOfNpcs", 0)
  }

  private fun setupRules() {
    /*
    These rules are dumb: we shall also track WHOM we
    are meeting, in the form of a contextual fact
    for that agent.

    So all agents need a key. Yay!

    Later for that though
     */

    RulesOfTheWorld.addRule(Rule("FirstMeetingWithNPC", mutableListOf(
        Criterion.context(Contexts.MetNpc)),
        ConversationConsequence("conversations/beamon_memory.ink.json")))
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
    engine.addSystem(PlayerEntityDiscoverySystem(playerEntity))

    engine.addSystem(CharacterControlSystem())

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
    for (system in engine.systems.filter { it !is RenderCharactersSystem && it !is RenderMapSystem }) {
      system.setProcessing(false)
      if (system is CharacterControlSystem) {
        system.processInput = false
      }

    }
  }

  private fun resumeWorldMap() {
    hud.hideInventory()
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