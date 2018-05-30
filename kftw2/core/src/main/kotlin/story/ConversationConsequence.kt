package story

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.GameSettings
import injection.Ctx

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

class ConversationConsequenceBuilder : Builder<ConversationConsequence> {
  var storyPath: String = ""
  override fun build(): ConversationConsequence {
    return ConversationConsequence(storyPath)
  }

}