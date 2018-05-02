package managers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.managers.Messages

class MessageManager: Telegraph {
  override fun handleMessage(msg: Telegram): Boolean {
    when(msg.message) {
      Messages.CollidedWithImpassibleTerrain -> return NpcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      Messages.PlayerMetSomeone -> return PlayerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
    }
    return false
  }

  private fun PlayerEncounteredNpc(npc: Npc): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private fun NpcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}