package story.conversation

import data.EmptyAgent
import data.IAgent
import data.Player
import injection.Ctx
import story.Builder
import story.FactsOfTheWorld
import javax.print.DocFlavor

class InlineConvo(override val protagonist: IAgent, override val antagonist: IAgent = EmptyAgent(), val antagonistLines: Map<Int, List<String>> = mapOf()) : IConversation {

  private var storyIndex = 0
  val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

  override val antagonistCanSpeak: Boolean
    get() = storyIndex >= 0 && storyIndex < antagonistLines.keys.count()
  override val protagonistCanChoose: Boolean
    get() = !quit
  override val choiceCount: Int
    get() = 4

  override fun getAntagonistLines(): Iterable<String> {
    if(storyIndex < 0)
      quit = true
    return if(!quit) getForIndex(storyIndex) else emptyList()
  }

  private fun getForIndex(index: Int): Iterable<String> {
    return antagonistLines[index]!!
  }

  override fun getProtagonistChoices(): Iterable<String> {
    return setOf("Ja", "Nej", "Driver du med mig?", "Hejdå")
  }

  private var quit: Boolean = false

  override fun makeChoice(index: Int): Boolean {
    when(index) {
      0 -> storyIndex++//Ja
      1 -> storyIndex--//Nej
      2,3 -> quit = true
    }
    return true
  }

}

class InternalConversation(private val startingStepKey:String, private val conversationSteps: Map<String, ConversationStep>, override val antagonist: IAgent = EmptyAgent()) :IConversation {

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
      //try to set a new step?
      val newStep = conversationSteps[currentStep.conversationRoutes.map { it.key }.toTypedArray()[index]]
      if(newStep != null) {
        currentStep = newStep
        return true
      }
    }
    return false
  }
}

fun convo(block: InternalConversationBuilder.() -> Unit) = InternalConversationBuilder().apply(block).build()

class InternalConversationBuilder : Builder<InternalConversation> {
	var startingStepKey = ""
	val steps = mutableMapOf<String, ConversationStep>()
	fun step(block: ConversationStepBuilder.() -> Unit) {
		val c = ConversationStepBuilder().apply(block).build()
		steps[c.key] = c
	}

	override fun build(): InternalConversation {
    if(!steps.containsKey("abort"))
      steps["abort"] =ConversationStep("abort", listOf("Tack för pratet"), emptyList())

   return InternalConversation(startingStepKey, steps)
  }
}

class ConversationStepBuilder() : Builder<ConversationStep> {
	var key = ""
	private val antagonistLines = mutableListOf<String>()
  private val conversationRoutes = mutableListOf<ConversationRoute>()

  fun addLine(line:String) {
    antagonistLines.add(line)
  }

  fun positive(key: String, text: String ="Ja") {
    if(!conversationRoutes.any { it.routeType == RouteType.positive })
      conversationRoutes.add(ConversationRoute(key, text, RouteType.positive))
  }

  fun negative(key:String, text: String = "Nej") {
    if(!conversationRoutes.any {it.routeType == RouteType.negative})
      conversationRoutes.add(ConversationRoute(key, text, RouteType.negative))
  }
  fun rude(key:String, text: String = "Far åt helvete!") {
    if(!conversationRoutes.any {it.routeType == RouteType.rude})
      conversationRoutes.add(ConversationRoute(key, text, RouteType.rude))
  }
  fun abort(key:String, text: String = "Avsluta") {
    if(!conversationRoutes.any {it.routeType == RouteType.abort})
      conversationRoutes.add(ConversationRoute(key, text, RouteType.abort))
  }

  override fun build(): ConversationStep = ConversationStep(key, antagonistLines, conversationRoutes)
}

class ConversationStep(
    val key:String,
    val antagonistLines: Iterable<String>,
    val conversationRoutes: Iterable<ConversationRoute>)

class ConversationRoute(val key:String,
                        val text:String = "Ja",
                        val routeType: RouteType = RouteType.positive) {
}

enum class RouteType {
  positive,
  negative,
  rude,
  abort
}
