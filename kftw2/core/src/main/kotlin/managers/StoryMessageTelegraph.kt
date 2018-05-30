package managers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import story.StoryManager

class StoryMessageTelegraph(private val gameState: GameState, private val storyManager: StoryManager): Telegraph {
//  private val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  override fun handleMessage(msg: Telegram): Boolean {
  if(msg.message !in StoryMessages.validRange) return true
    when(msg.message) {
      StoryMessages.FactsUpdated -> storyManager.checkStories() //this method will trigger all stories to check if their rules have passed, for instance
      StoryMessages.StoryCompleted -> return true //this method will trigger the "story ended" thingie related to a story... ending. Might not be relevant
    }
    return true
  }

}