package managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import data.Npc
import injection.Ctx
import story.FactsOfTheWorld
import story.StoryManager
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
class MessageTelegraph (private val factsOfTheWorld: FactsOfTheWorld): Telegraph {

  private val messageDispatcher by lazy { Ctx.context.inject<MessageDispatcher>() }

  private val storyManager by lazy { Ctx.context.inject<StoryManager>() }

  override fun handleMessage(msg: Telegram): Boolean {
    if(msg.message !in Messages.validRange) throw IllegalArgumentException("Message id ${msg.message} not in valid range ${Messages.validRange}")
    when(msg.message) {
      Messages.CollidedWithImpassibleTerrain -> return npcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      Messages.PlayerMetSomeone -> return playerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
      Messages.EncounterOver -> encounterOver()
      Messages.FactsUpdated -> storyManager.checkStories() //this method will trigger all stories to check if their rules have passed, for instance
      Messages.StoryCompleted -> return true //this method will trigger the "story ended" thingie related to a story... ending. Might not be relevant
    }
    return true
  }

  private fun encounterOver() {
    factsOfTheWorld.clearFacts(setOf(Facts.Context, Facts.CurrentNpc, Facts.CurrentNpcName))
    messageDispatcher.dispatchMessage(Messages.FactsUpdated)
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
    messageDispatcher.dispatchMessage(Messages.FactsUpdated)
    return true
  }

  private fun npcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}

