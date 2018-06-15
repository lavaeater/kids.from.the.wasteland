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
import factory.ActorFactory
import ktx.actors.keepWithinParent
import ktx.scene2d.KTableWidget
import ktx.scene2d.table
import managers.GameEvents
import managers.GameState
import story.FactsOfTheWorld
import story.conversation.IConversation
import story.fact.Facts

class UserInterface(
    private val batch: Batch,
    private val gameState: GameState,
    private val inputManager: InputMultiplexer,
    private val factsOfTheWorld: FactsOfTheWorld,
    debug: Boolean = false): IUserInterface {
  override fun showCombat() {
    /*
    So, combat, how's that gonna work?

    What do I want with a combat system? That is more important than anything else.

    What is it that I DON'T WANT with a combat system?

    Well, my game is supposed to be about storytelling.

    I want to be able to tell a tale while a player is exploring some strange world.
    In this world, combat will happen from time to time - but I don't want combat to
    be...
    * tedious
    * drawn out
    * repetitive
    It should be fun but dangerous? Does that make sense?
    How about combat basically always having the option of quitting?
    This goes for everyone, npc:s as well as players. Running away makes
    you drop something but you live to fight another day.
    Npc:s that attack the player but realize they should run away, they
    add some fear to their behaviour regarding the player, which is a cool
    concept.

    So, rock-paper-scissors it is, meaning
    that all attacks are better or worse at other attacks / defenses
    All moves have defense built into them - we could imagine something
    like "Heavy Attack Parry and Counter" which would be able to block a heavy attack
    from an attacker AND potentially do a counter to his attack - if a heavy attack is
    actually performed. If not, the player might be able to block the actual attack but not
    perform a counter. Yay!

    So, if an action is "defense first", the player / npc waits for the other persons attack, which
    affects initiative and stuff. This is cool.

    Apart from that, do the JRP article thingie...
     */
    var combatUI = CombatPresenter(stage, setOf(ActorFactory.npcByKeys.values.first()))
    combatUI.runCombat()
  }

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


  override fun runConversation(
      conversation: IConversation,
      conversationEnded: () -> Unit,
      showProtagonistPortrait: Boolean,
      showAntagonistPortrait:Boolean) {

    conversationUi = ConversationPresenter(
        stage,
        conversation, {
      conversationUi.dispose()
      conversationEnded()
    },
        showProtagonistPortrait,
        showAntagonistPortrait)
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
    }, 0.5f)
  }


  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}