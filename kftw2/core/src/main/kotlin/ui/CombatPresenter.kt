package ui

import Assets
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import data.EmptyAgent
import data.IAgent
import data.Player
import injection.Ctx
import ktx.actors.keepWithinParent
import ktx.actors.onChange
import ktx.actors.onKey
import ktx.scene2d.KTableWidget
import ktx.scene2d.table
import ktx.scene2d.textButton
import statemachine.StateMachine

interface StageFace {
  /*
  ooh... what do we need this for? Well, we could wrap a stage in this interface
  for mocking purposes, making it possible for us to write
  tests for all of this!

  Maybe later, though
   */
}

class CombatPresenter(private val stage:Stage, val targets: Set<IAgent> = emptySet()) {

  val combatActions = mutableListOf<CombatAction>()
  val combatTable = table {

  }
  val combatState = StateMachine.buildStateMachine<CombatState, CombatEvent>(CombatState.NotStarted, ::CombatStateChanged, {
    state(CombatState.NotStarted) {
      edge(CombatEvent.Start, CombatState.Tick) {}
    }
    state(CombatState.Tick) {
      edge(CombatEvent.Ticked, CombatState.TryExecute) {}
      edge(CombatEvent.End, CombatState.Ended) {}
    }
    state(CombatState.TryExecute) {
      edge(CombatEvent.Execute, CombatState.Executing) {}
      edge(CombatEvent.Executed, CombatState.Tick) {}
      edge(CombatEvent.End, CombatState.Ended) {}
    }
    state(CombatState.Executing) {
      edge(CombatEvent.Executed, CombatState.Tick) {}
      edge(CombatEvent.End, CombatState.Ended) {}
    }
  })

  val combatIsOver: Boolean get() = false

  private val player = Ctx.context.inject<Player>()
  private val playerSelectAction = PlayerSelectAction(
      player,
      targets,
      stage,
      ::actionSelected)

  init {
    /*
    The player needs a choice action, and all the targets too!
     */
    combatActions.add(playerSelectAction)
    for (target in targets)
      combatActions.add(AiSelectAction(target, player, stage, ::actionSelected))
  }


  private fun CombatStateChanged(state: CombatState) {
    when(state) {
      CombatState.Tick -> tickTheActions()
      CombatState.TryExecute -> tryToExecuteABitch()
      CombatState.Ended -> endCombat()
      CombatState.NotStarted -> combatState.acceptEvent(CombatEvent.Start)
      else -> {} //The executing state is just a "hold" state, nothing is done with that handling (it's been started already)
    }
  }

  private fun endCombat() {
    //What to do, what to do...
  }

  private fun tryToExecuteABitch() {
    if(combatActions.any() && combatActions.first().isReady) {
      combatState.acceptEvent(CombatEvent.Execute)
      executeAction(combatActions.popTop())
    } else {
      combatState.acceptEvent(CombatEvent.Executed)
    }
    if(combatIsOver) {
      combatState.acceptEvent(CombatEvent.End)
    } else {

    }
  }

  private fun executeAction(action: CombatAction) {
    //Do the action!
    var result = action.execute()
    /*
    Empty results are managed with a callback, I suppose?
    The type of result should be "immediate" or something...

    but more on that later!
     */
    if(result.type != CombatActionResultType.empty) {
      presentResult(result)
      combatActionExecuted(action.agent)
    }
  }

  private fun presentResult(result: CombatActionResult) {
    //Show a blurb with some delay or something...
    var text = result.actionText
  }

  private fun tickTheActions() {
    for (action in combatActions) {
      action.tick()
    }
    combatActions.sortBy { it.ticks }
    combatState.acceptEvent(CombatEvent.Ticked)
  }


  fun runCombat() {
    combatTable.isVisible = true
    combatState.initialize()
  }




  fun combatActionExecuted(agent: IAgent) {
    if(agent is Player)
      combatActions.add(playerSelectAction)
    else {
      combatActions.add(AiSelectAction(agent, player, stage, ::actionSelected))
    }

    combatState.acceptEvent(CombatEvent.Executed)
  }

  private fun actionSelected(selectedAction: CombatAction) {
    combatActions.add(selectedAction)
    combatState.acceptEvent(CombatEvent.Executed)
  }
}

class AiSelectAction(
    agent: IAgent,
    target:IAgent,
    stage:Stage,
    selectionMade: (CombatAction) -> Unit):
    SelectAction(
        agent,
        setOf(target),
        listOf(
    "Bits med tänderna",
    "Hugger med klorna",
    "Slår med svansen"),
        stage,
        selectionMade) {

  override fun execute(): CombatActionResult {
    selectionMade(CombatAction(agent,stage, MathUtils.random(5, 35), target))
    return CombatActionResult()
  }
}

open class SelectAction(agent:IAgent, val targets: Set<IAgent>, val choices: List<String>, stage:Stage, val selectionMade: (CombatAction) -> Unit) : CombatAction(
    agent,
    stage,
    0) {
  /*
    Ahh, geez, this shit is insane... what does execute even mean in this context?
    Do we need a bunch of callbacks and shit?
     Execute for a select action means:

     show the ui.

     wait for input

     fire "executed" event.

     So, until the player as actually
     made a selection, the choices will be shown..

     We only need one instance of this class, actually
     */
}

class PlayerSelectAction(
    agent: IAgent,
    targets: Set<IAgent>,
    stage: Stage,
    selectionMade: (CombatAction)->Unit)
  : SelectAction(agent,targets, listOf(
"Skjut med skjutaren",
"Hugg med huggaren",
"Stick med stickaren"), stage, selectionMade)
{
  lateinit var choiceTable: KTableWidget
  var root: KTableWidget

  private val speechBubbleNinePatch = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = speechBubbleNinePatch }
  private val baseWidth = UserInterface.uiWidth / 2
  init {
    root= table {
      choiceTable =  table {
        background = speechBubbleNinePatch
        keepWithinParent()
        left()
        bottom()
      }.cell(expandY = true, width = baseWidth, align = Align.bottomRight, padLeft = 16f, padBottom = 2f)
        row()
        image(Assets.beamonHeadshots["WilliamHamparsomian"]!!) {
          setScaling(Scaling.fit)
          keepWithinParent()
        }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft, pad = 2f, colspan = 2)
      isVisible = false
      pack()
    }
    stage.addActor(root)


    stage.keyboardFocus = root

    root.onKey { key ->
      if (key.isDigit() && key in '0'..'9') {
        makeChoice(key.toNumber())
      }
    }
  }

  fun showChoices() {
    clearChoices()
    choiceTable.apply {
      choices.withIndex().forEach { indexedValue ->
        val text = "${indexedValue.index}: ${indexedValue.value}"
        val button = textButton(text)
        button.onChange {
          makeChoice(indexedValue.index)
        }
        button.label.setWrap(true)
        add(button).align(Align.left).expandY().growX().pad(8f).space(4f).row()
        button.keepWithinParent()
      }
    }
    root.pack()
    root.isVisible = true
    root.invalidate()
  }

  private fun makeChoice(index: Int) {
    if(index in 0..choices.count() - 1) {
      clearChoices()
      clearStage()
    }
    when(index) {
      0 -> selectionMade(CombatAction(agent, stage, 20))
      1 -> selectionMade(CombatAction(agent, stage, 10))
      2 -> selectionMade(CombatAction(agent, stage, 5))
    }
  }

  fun clearChoices() {
    choiceTable.clearChildren()

  }

  fun clearStage() {
    root.isVisible = false //Not entirely sure how to handle this, we'll get back to it.
  }

  override fun execute() : CombatActionResult {
    showChoices()
    return CombatActionResult()
    //We do not call super execute, we call selectionmade... when we're done!
  }
}

open class CombatAction(
    val agent: IAgent,
    val stage: Stage,
    var ticks:Int = 10,
    val target: IAgent = EmptyAgent()) {
  val isReady get() = ticks < 1
  fun tick() {
    ticks--
  }

  open fun execute() : CombatActionResult {
    return CombatActionResult("Some attack was made", CombatActionResultType.attack)
  }
}

open class CombatActionResult(val actionText: String = "Action", val type: CombatActionResultType = CombatActionResultType.empty) {

}

enum class CombatActionResultType {
  empty,
  attack
}

enum class CombatEvent {
  Ticked,
  Executed,
  Start,
  End,
  Execute
}

enum class CombatState {
  NotStarted,
  Tick,
  TryExecute,
  Ended,
  Executing
}

fun <E> MutableList<E>.popTop() : E {
  return this.removeAt(0)
}
