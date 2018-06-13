package managers

import Assets
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import factory.ActorFactory
import map.IMapManager
import story.FactsOfTheWorld
import systems.GameInputSystem
import systems.RenderCharactersSystem
import systems.RenderFeatureSystem
import systems.RenderMapSystem
import ui.IUserInterface
import kotlin.math.roundToInt

class GameManager(
    gameState: GameState,
    private val batch: Batch,
    private val camera: Camera,
    viewPortProvider: () -> Viewport,
    private val engine: Engine, //Engine will come with all systems added already, yay!
    actorFactoryProvider: () -> ActorFactory,
    private val messageDispatcher: MessageDispatcher,
    private val ui: IUserInterface,
    private val mapManager: IMapManager,
    private val factsOfTheWorld: FactsOfTheWorld) : Disposable {

  private val viewPort = viewPortProvider()
  private val actorFactory = actorFactoryProvider()

  init {
    gameState.addChangeListener(::gameStateChanged)
    setupSystems()
    camera.position.x = 0f
    camera.position.y = 0f

    Assets.music.play()
  }

  private fun setupSystems() {
//    addEmployees()
  }

  private fun addEmployees() {

    /*
    Could be moved to some kind of init class or something, so the game manager manages a
    running game, and some other class, called during startup, sets up the state using all the
    dependencies necessary for that.
     */


    for (name in factsOfTheWorld.npcNames.values) {
      val someTilesInRange = mapManager.getBandOfTiles(0,0, 50, 3).filter {
        it.tile.tileType != "rock" && it.tile.tileType != "water"
      }

      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
      actorFactory.addNpcAtTileWithAnimation(name = name,type = "orc", spriteKey =  name.replace(" ", "").toLowerCase(), x = randomlySelectedTile.x, y = randomlySelectedTile.y)
    }
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
    factsOfTheWorld.save()
  }

  companion object {
    var VIEWPORT_HEIGHT = 64f
    var VIEWPORT_WIDTH = 48f
    var TILE_SIZE = 8
    val WIDTH_IN_TILES: Int get() = (GameManager.VIEWPORT_WIDTH / GameManager.TILE_SIZE).roundToInt()
    val VISIBLE_RANGE: Int get() = GameManager.WIDTH_IN_TILES / 2
  }

  fun pause() {
    factsOfTheWorld.save()
  }

  private fun gameStateChanged(newState: GameStates) {
    when(newState){
      GameStates.WorldMap -> resumeWorldMap()
      GameStates.Inventory -> showInventory()
      GameStates.Combat -> doFighting()
      GameStates.Dialog -> showDialog()
      GameStates.SplashScreen -> showSplashScreen()
      else -> {
        //These aren't defined yet!
      }
    }
  }

  private fun doFighting() {
    stopTheWorld()
    ui.showCombat()
  }

  private fun showSplashScreen() {
    stopTheWorld()
    ui.showSplashScreen()
  }

  private fun showInventory() {
    stopTheWorld()
    ui.showInventory()
  }

  private fun showDialog() {
    stopTheWorld()
  }

  private fun stopTheWorld() {
    for (system in engine.systems.filter {
      it !is RenderCharactersSystem &&
          it !is RenderMapSystem &&
          it !is RenderFeatureSystem }) {
      system.setProcessing(false)
      if (system is GameInputSystem) {
        system.processInput = false
      }

    }
  }

  private fun resumeWorldMap() {
    resumeTheWorld()
  }

  private fun resumeTheWorld() {
    for (system in engine.systems) {
      system.setProcessing(true)
      if(system is GameInputSystem)
      {
        system.processInput = true
      }
    }
  }
}