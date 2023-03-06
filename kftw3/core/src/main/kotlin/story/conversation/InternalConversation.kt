package story.conversation

import data.EmptyAgent
import data.IAgent
import data.Player
import injection.Ctx

class InternalConversation(
    startingStepKey:String,
    private val conversationSteps: Map<String, ConversationStep>,
    override val antagonist: IAgent = EmptyAgent(),
    val onChoice: (ConversationRoute) -> Unit)
  : IConversation {

  var currentStep: ConversationStep = conversationSteps[startingStepKey]!!

  override val protagonist: IAgent by lazy { Ctx.context.inject<Player>() }
  override val antagonistCanSpeak: Boolean
    get() = currentStep.antagonistLines.any()
  override val protagonistCanChoose: Boolean
    get() = currentStep.conversationRoutes.any()
  override val choiceCount: Int
    get() = currentStep.conversationRoutes.count()

  override fun getAntagonistLines(): Iterable<String> {
    return currentStep.antagonistLines
  }

  override fun getProtagonistChoices(): Iterable<String> {
    return currentStep.conversationRoutes.map { it.text }
  }

  override fun makeChoice(index: Int): Boolean {
    if(index >= 0 && index < currentStep.conversationRoutes.count()) {

      /*
      analyze the step. Is it an Abort?
      Is it Positive, Negative or Neutral?

      Lets say we create some flags or something that we can actually use with the
      types of responses?
       */

      val chosenRoute = currentStep.conversationRoutes.elementAt(index)

      onChoice(chosenRoute) //callback enabling some other code to send messages and whatnot.

      if(chosenRoute.routeType == RouteType.Exit) {
        //This conversation is over. There might be consequences, obviously.
        currentStep = EmptyStep() //An empty step containing no routes no nothing.
        return true //Yay!
      }

      //try to set a new step?
      val newStep = conversationSteps[chosenRoute.key]
      if(newStep != null) {
        currentStep = newStep
        return true
      }
    }
    return false
  }
}