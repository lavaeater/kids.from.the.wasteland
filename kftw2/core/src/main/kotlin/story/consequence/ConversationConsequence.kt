package story.consequence

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.GameSettings
import injection.Ctx
import story.conversation.InkLoader
import story.rule.Rule
import story.fact.IFact

class ConversationConsequence(private val storyPath:String = "ink/dialog.ink.json"): RetrieveConsequence<Story> {
	private val basePath by lazy { Ctx.context.inject<GameSettings>().assetBaseDir}
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ConversationLoader
  private val storyReader = InkLoader()
  override fun retrieve(): Story {
    return Story(storyReader.readStoryJson("$basePath/$storyPath"))
  }
}
