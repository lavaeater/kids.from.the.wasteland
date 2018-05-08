package managers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.managers.Messages
import story.DialogManager

class MessageManager: Telegraph {
  val gameStateManager = Ctx.context.inject<GameStateManager>()
  val dialogManager = Ctx.context.inject<DialogManager>()
  override fun handleMessage(msg: Telegram): Boolean {
    when(msg.message) {
      Messages.CollidedWithImpassibleTerrain -> return NpcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      Messages.PlayerMetSomeone -> return PlayerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
    }
    return false
  }

  private fun PlayerEncounteredNpc(npc: Npc): Boolean {

    /*
    We need a... dialog manager!
     */

    gameStateManager.handleEvent(GameEvent.DialogStarted)
    return true
  }

  private fun NpcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}