package managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import data.Npc
import story.FactsOfTheWorld
import story.fact.Contexts
import story.fact.Facts


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
class EncounterTelegraph(
    private val factsOfTheWorld: FactsOfTheWorld,
    private val messageDispatcher: MessageDispatcher): Telegraph {
  override fun handleMessage(msg: Telegram): Boolean {
    if(msg.message !in EncounterMessages.validRange) return true
    when(msg.message) {
      EncounterMessages.CollidedWithImpassibleTerrain -> return npcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      EncounterMessages.PlayerMetSomeone -> return playerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
      EncounterMessages.EncounterOver -> encounterOver()
    }
    return true
  }

  private fun encounterOver() {
    factsOfTheWorld
    factsOfTheWorld.clearFacts(setOf(Facts.Context, Facts.CurrentNpc, Facts.CurrentNpcName))
    messageDispatcher.dispatchMessage(StoryMessages.FactsUpdated)
  }

  private fun playerEncounteredNpc(npc: Npc): Boolean {

    /*
    This method shall set some facts.

    Then this method shall send the "facts have been updated"-message
    Game state will be updated by the dialog manager, I believe. - it has a reference to the state manager
     */
    factsOfTheWorld.stateStringFact(Facts.Context, Contexts.MetNpc)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpc, npc.id)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpcName, npc.name)
    messageDispatcher.dispatchMessage(StoryMessages.FactsUpdated)
    return true
  }

  private fun npcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}

