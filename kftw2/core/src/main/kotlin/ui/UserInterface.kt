package ui

import Assets
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.keepWithinParent
import ktx.scene2d.KTableWidget
import ktx.scene2d.table
import managers.GameEvents
import managers.GameState
import story.fact.Facts
import story.FactsOfTheWorld
import story.conversation.IConversation

class UserInterface(
    private val batch: Batch,
    private val gameState: GameState,
    private val inputManager: InputMultiplexer,
    private val factsOfTheWorld: FactsOfTheWorld,
    debug: Boolean = false): IUserInterface {

  override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
      .apply {
    isDebugAll = debug
  }

  companion object {
    private const val aspectRatio = 16 / 9
    const val uiWidth = 800f
    const val uiHeight = uiWidth * aspectRatio
  }

  private lateinit var conversationUi: IConversationPresenter
  private val labelStyle = Label.LabelStyle(Assets.standardFont, Color.WHITE)
  private lateinit var scoreLabel: Label


  override fun runConversation(conversation: IConversation, conversationEnded: () -> Unit) {
    conversationUi = ConversationPresenter(stage, conversation, {
      conversationUi.dispose()
      conversationEnded()
    })
  }

  init {
    setup()
  }

  override fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined
    updateScore()
    stage.act(delta)
    stage.draw()
  }

  private var score = 0

  private fun updateScore() {
    val tempScore = factsOfTheWorld.getIntValue(Facts.Score)
    if (tempScore != score) {
      score = tempScore
      scoreLabel.setText("Score: $score")
    }
  }

  override fun dispose() {
    stage.dispose()
  }

  override fun clear() {
    stage.clear()
  }


  private fun setup() {
    stage.clear()
    inputManager.addProcessor(stage)

    setUpScoreBoard()
  }

  private lateinit var rootTable: KTableWidget

  private lateinit var scoreBoard: KTableWidget

  private fun setUpScoreBoard() {
    scoreBoard = table {
      scoreLabel = label("Score: $score", labelStyle) {
        setWrap(true)
        keepWithinParent()
      }.cell(fill = true, align = Align.bottomLeft, padLeft = 16f, padBottom = 2f)
      isVisible = true
      pack()
      width = 300f

    }

    rootTable = table {
      setFillParent(true)
      bottom()
      left()
      add(scoreBoard).expand().align(Align.bottomLeft)
    }

    stage.addActor(rootTable)
  }

  override fun showSplashScreen() {
    /*
    Set up timer. Show splash screen.
    When timer fires, remove splash screen, send resume game event. Yay!
     */

    val splashScreen = table {
      image(Assets.splashScreen) {
        setScaling(Scaling.fit)
        scaleBy(4.0f)
      }.cell()
      setFillParent(true)
      isVisible = true
      bottom()
      left()
    }
    stage.addActor(splashScreen)
    Timer.instance().clear()

    Timer.instance().scheduleTask(object : Timer.Task() {
      override fun run() {
        stage.actors.removeValue(splashScreen, true)
        gameState.handleEvent(GameEvents.GameResumed)
      }
    }, 3f)
  }


  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}