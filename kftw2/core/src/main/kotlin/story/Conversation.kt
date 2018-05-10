package story

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.data.IAgent

class Conversation(val story:Story, override val protagonist:IAgent, override val antagonist:IAgent) : IConversation {
  var _state = ConversationState.AntagonistHasMoreToSay

  override val state: ConversationState
    get() = _state

  override val choiceCount: Int
    get() = story.currentChoices.size

  override fun getNextAntagonistLine(): String {
    if(story.canContinue()) {
      _state = ConversationState.AntagonistHasMoreToSay
      return story.Continue()
    }
    if(!story.canContinue() && story.currentChoices.any()) {
      _state = ConversationState.ProtagonistMustChoose
      return ""
    }
    if(!story.canContinue() && story.currentChoices.size == 0)
    {
      _state = ConversationState.Ended
    }
    return ""
  }

  override fun getProtagonistChoices(): Iterable<String> {
    if(_state == ConversationState.ProtagonistMustChoose)
    {
      _state = ConversationState.ProtagonistChoosing
      return story.currentChoices.map { it.text }
    }
    return emptyList()
  }

  override fun makeChoice(index: Int): Boolean {
    if(_state == ConversationState.ProtagonistChoosing && index in 0..story.currentChoices.size -1) {
      story.chooseChoiceIndex(index)
      _state = ConversationState.AntagonistHasMoreToSay
      return true
    }
    return false
  }
}