package world

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.ui.IUserInterface


class Facts {
  companion object {
    const val Context ="Context"
    const val NpcsPlayerHasMet = "NpcsPlayerHasMet"
    const val CurrentNpc = "CurrentNpc"
    const val MetNumberOfNpcs ="MetNumberOfNpcs"
    val VisitedPlaces = "VisitedPlaces"
    val FoundKey = "FoundKey"
    val MetOrcs = "FoundKey"
    val NumberOfVisitedPlaces = "NumberOfVisitedPlaces"
  }
}

class Contexts {
  companion object {
    const val MetNpc = "MetNpc"
  }
}

class ConversationManager {
  private val ui = Ctx.context.inject<IUserInterface>()
  private val gameStateManager = Ctx.context.inject<GameStateManager>()
  private var currentStory: Story? = null
  private var currentAgent:IAgent? = null
  private val player = Ctx.context.inject<Player>()

  fun startWithNpc(npc:Npc) {
    /*

    Glory.

    Everything must be set in the facts of the world.

    So we start with setting the current Context.

    THEN we find the rules. Fantastic. Glorious
     */

    //Add to list of agents player has met
    FactsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, npc.id)
    FactsOfTheWorld.stateStringFact(Facts.Context, Contexts.MetNpc)
    FactsOfTheWorld.stateStringFact(Facts.CurrentNpc, npc.id)

    /**
     * Aaah, the remnants!
     *
     * Find some rules that match the current Context, which is "MetNpc" etc etc
     */

    //This is the simple Context, just a string for like an event or something
    //The Context will probably be a bunch of stuff, like who the npc is and stuff.
    val rules = FactsOfTheWorld.rulesThatPass(RulesOfTheWorld.rules)
        .filter {
          it.consequence.consequenceType == ConsequenceType.ConversationLoader
        }

    //We should probably let rules have lists of consequences, maybe?
    if(rules.any()) {
      /*
      So how do we make a rule load a conversation?
      How do we know it can?
       */
      //We *know* that the type is conversationconsequence, so we'll just
      //retrieve the story, yay!
      currentAgent = npc
      currentStory = (rules.first().consequence as ConversationConsequence).retrieve()
      ui.runConversation(Conversation(currentStory!!, player, npc), {endConversation(npc)})
    } else {
      endConversation(npc)
    }
  }

  private fun endConversation(npc:Npc) {

    //Add to list of agents player has met
    FactsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, npc.id)
    //Add to counter of this particular type
    FactsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)
    FactsOfTheWorld.clearStringFact(Facts.CurrentNpc)

    currentAgent = null
    currentStory = null
    gameStateManager.handleEvent(GameEvent.DialogEnded)
  }
}

enum class ConsequenceType {
  ConversationLoader,
  ApplyFactsConsequence,
  ApplyLambdaConsequence
}
