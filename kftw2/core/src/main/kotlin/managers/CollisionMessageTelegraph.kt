package managers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import data.Npc
import injection.Ctx
import story.ConversationManager


/**
 * This class listens to messages related to... collisions!
 *
 * This is great. There should not be a master / monster / god
 * class that handles all the messages, that seems a bit weird.
 *
 * No, we should have multiple, independet, Telegraphs, that listen
 * to a particular SET of messages and handle them independently.
 *
 * This makes it easire to contain their responsibilities.
 */
class CollisionMessageTelegraph(private val gameState: GameState): Telegraph {
  private val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  override fun handleMessage(msg: Telegram): Boolean {
    if(msg.message !in CollisionMessages.validRange) return true
    when(msg.message) {
      CollisionMessages.CollidedWithImpassibleTerrain -> return npcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      CollisionMessages.PlayerMetSomeone -> return playerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
    }
    return true
  }

  private fun playerEncounteredNpc(npc: Npc): Boolean {

    gameState.handleEvent(GameEvents.DialogStarted)
    conversationManager.startWithNpc(npc)
    return true
  }

  private fun npcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}

class StoryMessageTelegraph(private val gameState: GameState): Telegraph {
//  private val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  override fun handleMessage(msg: Telegram): Boolean {
  if(msg.message !in StoryMessages.validRange) return true
    when(msg.message) {
      StoryMessages.FactsUpdated-> return true //this method will trigger all stories to check if their rules have passed, for instance
      StoryMessages.StoryCompleted -> return true //this method will trigger the "story ended" thingie related to a story... ending.
    }
    return true
  }

}