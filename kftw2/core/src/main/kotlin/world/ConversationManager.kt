package world

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.ui.IUserInterface

class ConversationManager {
  private val ui = Ctx.context.inject<IUserInterface>()
  private val gameStateManager = Ctx.context.inject<GameStateManager>()
  private var currentStory: Story? = null
  private var currentAgent:IAgent? = null
  private val player = Ctx.context.inject<Player>()

  fun startWithNpc(npc:Npc) {
    /**
     * Aaah, the remnants!
     *
     * Find some rules that match the current context, which is "MetNpc" etc etc
     */

    //This is the simple context, just a string for like an event or something
    //The context will probably be a bunch of stuff, like who the npc is and stuff.
    val rules = FactsOfTheWorld.rulesThatPass(RulesOfTheWorld.rules, "MetNpc")
        .filter {
          it.consequence.consequenceType == ConsequenceType.ConversationLoader
        }
        .union(FactsOfTheWorld.rulesThatPass(RulesOfTheWorld.rules, setOf(Fact.createFact("Context", "MetNpC"),
            Fact.createFact("NpcsPlayerHasMet", npc))))

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
    FactsOfTheWorld.addValueToFactList("NpcsPlayerHasMet", npc)
    //Add to counter of this particular type
    FactsOfTheWorld.addToIntFact("MetNumberOfNpcs", 1)


    currentAgent?.stateFact(Fat.MetPlayer)

    currentAgent = null
    currentStory = null
    gameStateManager.handleEvent(GameEvent.DialogEnded)
  }
}

enum class ConsequenceType {
  ConversationLoader
}
