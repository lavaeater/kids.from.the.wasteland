package managers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.managers.Messages
import story.ConversationManager

class MessageManager: Telegraph {
  val gameStateManager = Ctx.context.inject<GameStateManager>()
  val conversationManager = Ctx.context.inject<ConversationManager>()
  override fun handleMessage(msg: Telegram): Boolean {
    when(msg.message) {
      Messages.CollidedWithImpassibleTerrain -> return npcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      Messages.PlayerMetSomeone -> return playerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
    }
    return false
  }

  private fun playerEncounteredNpc(npc: Npc): Boolean {

    /*
    We need a... dialog manager!
     */

    gameStateManager.handleEvent(GameEvent.DialogStarted)
    conversationManager.startWithNpc(npc)
    return true
  }

  private fun npcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}