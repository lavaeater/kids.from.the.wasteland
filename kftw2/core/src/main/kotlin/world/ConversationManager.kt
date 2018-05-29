package world

import com.bladecoder.ink.runtime.Story
import data.IAgent
import data.Npc
import data.Player
import managers.GameEvents
import managers.GameState
import ui.IUserInterface


class ConversationManager(
    private val ui: IUserInterface,
    private val gameStateManager:GameState,
    private val player: Player,
    private val factsOfTheWorld: FactsOfTheWorld,
    private val rulesOfTheWorld: RulesOfTheWorld) {
  private var currentStory: Story? = null
  private var currentAgent: IAgent? = null

  fun startWithNpc(npc: Npc) {
    /*

    Glory.

    Everything must be set in the facts of the world.

    So we start with setting the current Context.

    THEN we find the rules. Fantastic. Glorious
     */

    //Add to list of agents player has met
    factsOfTheWorld.stateStringFact(Facts.Context, Contexts.MetNpc)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpc, npc.id)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpcName, npc.name)

    /**
     * Aaah, the remnants!
     *
     * Find some rules that match the current Context, which is "MetNpc" etc etc
     */

    //This is the simple Context, just a string for like an event or something
    //The Context will probably be a bunch of stuff, like who the npc is and stuff.
    val rules = factsOfTheWorld.rulesThatPass(rulesOfTheWorld.rules)
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
      ui.runConversation(InkConversation(currentStory!!, player, npc), {endConversation(npc)})

    } else {
      endConversation(npc)
    }
  }

  private fun endConversation(npc: Npc) {

    //Add to list of agents player has met
    factsOfTheWorld.addStringToList(Facts.NpcsPlayerHasMet, npc.id)
    //Add to counter of this particular type
    factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)
    factsOfTheWorld.clearStringFact(Facts.CurrentNpc)

    if (!factsOfTheWorld.factListForKey(Facts.KnownNames).contains(npc.name)
        && currentStory!!.variablesState["guessed_right"] as Int == 1) {
      factsOfTheWorld.addToIntFact(Facts.Score, 1)
      factsOfTheWorld.addStringToList(Facts.KnownNames, npc.name)
    }

    currentAgent = null
    currentStory = null
    gameStateManager.handleEvent(GameEvents.DialogEnded)
  }
}

enum class ConsequenceType {
  ConversationLoader,
  ApplyFactsConsequence,
  ApplyLambdaConsequence,
  Empty
}
