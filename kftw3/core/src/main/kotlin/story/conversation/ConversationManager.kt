package story.conversation

import managers.GameEvents
import managers.GameState
import ui.IUserInterface


class ConversationManager(
    private val ui: IUserInterface,
    private val gameState:GameState) {

  fun startConversation(
      conversation: IConversation,
      endConversation: ()-> Unit,
      showProtagonistPortrait: Boolean = true,
      showAntagonistPortrait: Boolean = true) {
    ui.runConversation(
        conversation, {
      endConversation()
      gameState.handleEvent(GameEvents.DialogEnded)
    },
        showProtagonistPortrait,
        showAntagonistPortrait)

  }
}

