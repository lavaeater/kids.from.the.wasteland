package world

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent

class Conversation(val story:Story, override val protagonist:IAgent, override val antagonist:IAgent) : IConversation {
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
    if(index in 0..story.currentChoices.size -1) {
      story.chooseChoiceIndex(index)
      return true
    }
    return false
  }
}