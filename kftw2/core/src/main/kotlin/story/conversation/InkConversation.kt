package story.conversation

import com.badlogic.gdx.math.MathUtils
import com.bladecoder.ink.runtime.Story
import data.IAgent
import injection.Ctx
import story.FactsOfTheWorld
import story.fact.Facts

class InkConversation(val story:Story, override val protagonist: IAgent, override val antagonist: IAgent) : IConversation {
  private val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>()}
  init {
  }
  override val antagonistCanSpeak: Boolean
    get() = story.canContinue()
  override val protagonistCanChoose: Boolean
    get() = story.currentChoices.size > 0

  override val choiceCount: Int
    get() = story.currentChoices.size

  override fun getAntagonistLines(): Iterable<String> {
    val lines = mutableListOf<String>()
    if(story.canContinue()) {
      while (story.canContinue()) {
        lines.add(story.Continue())
      }
    }
    return lines
  }

  override fun getProtagonistChoices(): Iterable<String> {
      return story.currentChoices.map { it.text }
  }

  override fun makeChoice(index: Int): Boolean {
    if(index in 0 until story.currentChoices.size) {
      story.chooseChoiceIndex(index)
      return true
    }
    return false
  }
}

