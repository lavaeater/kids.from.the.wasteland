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
import com.lavaeater.kftw.map.tileWorldCenter
import com.lavaeater.kftw.ui.Hud
import ktx.scene2d.dialog
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.BufferedReader
import java.io.InputStreamReader


class ConversationManager {
  private val hud = Ctx.context.inject<Hud>()
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
    if(lines.any())
      showStoryLines(lines)
    else
      endCurrentDialog()
  }

  fun showStoryLines(lines: List<String>) {
    dialogState = DialogState.ShowingDialog
    hud.showDialog(lines)
  }

  fun showChoices(choices: List<Choice>) {
    dialogState = DialogState.ShowingChoices
    hud.showChoices(choices.map { it.text })
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
