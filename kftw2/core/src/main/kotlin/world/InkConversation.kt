package world

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent

class InkConversation(val story:Story, override val protagonist:IAgent, override val antagonist:IAgent) : IConversation {
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

class FactConversation(override val protagonist: IAgent, override val antagonist: IAgent) : IConversation {
  override val antagonistCanSpeak: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val protagonistCanChoose: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val choiceCount: Int
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

  override fun getAntagonistLines(): Iterable<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getProtagonistChoices(): Iterable<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun makeChoice(index: Int): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}