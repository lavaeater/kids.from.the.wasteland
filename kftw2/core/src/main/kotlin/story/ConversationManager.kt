package story

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.ui.IUserInterface

class ConversationManager {
  private val ui = Ctx.context.inject<IUserInterface>()
  private val gameStateManager = Ctx.context.inject<GameStateManager>()
  private var currentStory: Story? = null
  private var currentAgent:IAgent? = null
  private val player = Ctx.context.inject<Player>()
  private val storyReader = InkLoader()

  fun startWithNpc(npc:Npc) {
    currentAgent = npc
    currentStory = Story(storyReader.readStoryJson("ink/dialog.ink.json"))
    ui.runConversation(Conversation(currentStory!!, player, npc), ::endConversation)
  }

  fun endConversation() {
    currentAgent = null
    currentStory = null
    gameStateManager.handleEvent(GameEvent.DialogEnded)
  }
}