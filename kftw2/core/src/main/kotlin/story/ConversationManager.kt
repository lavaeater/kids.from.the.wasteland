package story

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.ui.IHud


class ConversationManager {
  private val hud = Ctx.context.inject<IHud>()
  private val gameStateManager = Ctx.context.inject<GameStateManager>()
  private var currentDialog: Story? = null
  private val story get() = currentDialog!!
  private var currentAgent:IAgent? = null
  private val player = Ctx.context.inject<Player>()
  private var inputProcessor : InputProcessor? = null
  private val storyReader = InkLoader()

  fun startWithNpc(npc:Npc) {
    if(!isDialogOnGoing) {
      currentAgent = npc
      currentDialog = Story(storyReader.readStoryJson("ink/dialog.ink.json"))
      inputProcessor =  Gdx.input.inputProcessor
      hud.startDialog(::makeChoice)
      continueStory()
    }
  }

  fun endConversation() {
    if(isDialogOnGoing) {
      currentDialog = null
      currentAgent = null
      hud.hideDialog()
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
    if(lines.any())
      showStoryLines(lines)
    else
      endConversation()
  }

  fun showStoryLines(lines: List<String>) {
    dialogState = DialogState.ShowingDialog
    hud.showDialog(lines)
  }

  fun showChoices() {
    dialogState = DialogState.ShowingChoices
    if(story.currentChoices.any())
      hud.showChoices(story.currentChoices.map { it.text })
    else if(!story.canContinue())
      endConversation()
    else
      continueStory()

  }

  fun makeChoice(keyCode: Int) {
    when(dialogState) {
      DialogState.ShowingDialog -> showChoices()
      DialogState.ShowingChoices -> {
        if(keyCode !in 7..16) return //Not a numeric key!

        val index = keyCode - 7
        if(index !in 0..story.currentChoices.count() - 1) return //Out of range for correct choices, just ignore
        story.chooseChoiceIndex(index)
        continueStory()
      }
      DialogState.NotStarted -> return
    }
  }

  val isDialogOnGoing get() = currentDialog != null
}

class InkLoader {
  fun readStoryJson(path:String):String {

    val br= Gdx.files.internal(path).reader(100, "UTF-8")

    try {
      val sb = StringBuilder()
      var line = br.readLine()

      // Replace the BOM mark
      if (line != null)
        line = line!!.replace('\uFEFF', ' ')

      while (line != null) {
        sb.append(line)
        sb.append("\n")
        line = br.readLine()
      }
      return sb.toString()
    } finally {
      br.close()
    }
  }
}

enum class DialogState {
  NotStarted,
  ShowingDialog,
  ShowingChoices
}
