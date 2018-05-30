package story.consequence

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.GameSettings
import data.Player
import injection.Ctx
import story.FactsOfTheWorld
import story.conversation.ConversationManager
import story.conversation.InkConversation
import story.conversation.InkLoader
import story.fact.Facts
import story.fact.IFact
import story.rule.Rule

class ConversationConsequence(private val storyPath:String = "ink/dialog.ink.json"): RetrieveConsequence<Story> {

  val conversationManager by lazy { Ctx.context.inject<ConversationManager>()}
  val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }
  val player by lazy { Ctx.context.inject<Player>() }


  override fun apply() {

    var npc = factsOfTheWorld.getCurrentNpc()
    if(npc != null) {//If null something is weird
      val story = Story(storyReader.readStoryJson(storyPath))








      val convo = InkConversation(story, player, npc)

      conversationManager.startConversation(convo, {
        factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npc.id)
        //Add to counter of this particular type
        factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)

        if (!factsOfTheWorld.getFactList(Facts.KnownNames).contains(npc.name)
            && story!!.variablesState["guessed_right"] as Int == 1) {
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

  private val basePath by lazy { Ctx.context.inject<GameSettings>().assetBaseDir}
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
