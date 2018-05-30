package story.consequence

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.GameSettings
import data.Player
import injection.Ctx
import managers.GameEvents
import managers.GameState
import story.FactsOfTheWorld
import story.conversation.ConversationManager
import story.conversation.InkConversation
import story.conversation.InkLoader
import story.fact.Facts
import story.fact.IFact
import story.rule.Rule

class ConversationConsequence(private val storyPath:String = "ink/dialog.ink.json"): RetrieveConsequence<Story> {

  private val basePath by lazy { Ctx.context.inject<GameSettings>().assetBaseDir}
  private val gameState by lazy { Ctx.context.inject<GameState>() }
  private val conversationManager by lazy { Ctx.context.inject<ConversationManager>()}
  private val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }
  private val player by lazy { Ctx.context.inject<Player>() }
  private val path by lazy {"$basePath/$storyPath" }


  override fun apply() {

    val npc = factsOfTheWorld.getCurrentNpc()
    if(npc != null) {//If null something is weird
      gameState.handleEvent(GameEvents.DialogStarted)
      val story = Story(storyReader.readStoryJson(path))

      conversationManager.startConversation(InkConversation(story, player, npc), {
        factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npc.id)
        //Add to counter of this particular type
        factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)

        if (!factsOfTheWorld.getFactList(Facts.KnownNames).contains(npc.name)
            && story.variablesState["guessed_right"] as Int == 1) {
          factsOfTheWorld.addToIntFact(Facts.Score, 1)
          factsOfTheWorld.addToList(Facts.KnownNames, npc.name)
        }
      })
    }
    /*
    factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npc.id)
    //Add to counter of this particular type
    factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)
    factsOfTheWorld.clearStringFact(Facts.CurrentNpc)

    if (!factsOfTheWorld.getFactList(Facts.KnownNames).contains(npc.name)
        && currentStory!!.variablesState["guessed_right"] as Int == 1) {
      factsOfTheWorld.addToIntFact(Facts.Score, 1)
      factsOfTheWorld.addToList(Facts.KnownNames, npc.name)
    }
     */

  }

  /*

  This is an experiment.

  We will try to encapsulate everything that is needed to manage a conversation using ink
  into *this* class. We will lazily load a conversation manager and run the ui ON it... fucking
  great.

  For this particular thingie we will have all the facts that we know about the world...
  I would like to be able to load more facts, not hardcode them, but we will see what we
  need. First some testing of my actual job, though.
   */



  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ConversationLoader
  private val storyReader = InkLoader()
  override fun retrieve(): Story {
    return Story(storyReader.readStoryJson("$basePath/$storyPath"))
  }
}
