package story

import com.lavaeater.kftw.data.IAgent

interface IConversation {
  val state: ConversationState
  val protagonist: IAgent
  val antagonist: IAgent
  val choiceCount: Int

  fun getNextAntagonistLine():String
  fun getProtagonistChoices():Iterable<String>
  fun makeChoice(index:Int) : Boolean
}