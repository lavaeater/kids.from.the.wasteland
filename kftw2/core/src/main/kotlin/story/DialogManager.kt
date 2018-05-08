package story

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.bladecoder.ink.runtime.Choice
import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.ui.Hud
import ktx.scene2d.dialog

class DialogManager {
  private val hud = Ctx.context.inject<Hud>()
  private val gameStateManager = Ctx.context.inject<GameStateManager>()
  private var currentDialog: Story? = null
  private val story get() = currentDialog!!
  private var currentAgent:IAgent? = null
  private val player = Ctx.context.inject<Player>()
  private var inputProcessor : InputProcessor? = null
  fun startWithNpc(npc:Npc) {
    if(!isDialogOnGoing) {
      currentAgent = npc
      currentDialog = Story(Gdx.files.internal("ink/dialog.ink.json").readString())
      inputProcessor =  Gdx.input.inputProcessor
      hud.startDialog { ::makeChoice }
      continueStory()
    }
  }

  fun endCurrentDialog() {
    if(isDialogOnGoing) {
      currentDialog = null
      currentAgent = null
      Gdx.input.inputProcessor = inputProcessor
      dialogState = DialogState.NotStarted
      gameStateManager.handleEvent(GameEvent.DialogEnded)
    }
    /*
    WHat happens when the dialog ends?
    How do we change the game state back?
    We send an event, of course...
     */
  }

  var dialogState = DialogState.NotStarted

  fun continueStory() {
    val lines = mutableListOf<String>()
    while(story.canContinue()) {
      //Get current lines until we stop for choices.
      lines.add(story.Continue())
    }
    showStoryLines(lines)
  }

  fun showStoryLines(lines: List<String>) {
    hud.showDialog(lines, 100f, 100f)
  }

  fun showChoices(choices: List<Choice>) {
    hud.showChoices(choices.map { it.text }, 100f, 100f)
  }

  fun makeChoice(index: Int) {
    when(dialogState) {
      DialogState.ShowingDialog -> showChoices(story.currentChoices)
      DialogState.ShowingChoices -> {
        story.chooseChoiceIndex(index)
        continueStory()
      }
      DialogState.NotStarted -> return
    }
  }

  val isDialogOnGoing get() = currentDialog != null
}

enum class DialogState {
  NotStarted,
  ShowingDialog,
  ShowingChoices
}
