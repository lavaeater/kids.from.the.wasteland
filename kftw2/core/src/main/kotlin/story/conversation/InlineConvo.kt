package story.conversation

import data.EmptyAgent
import data.IAgent
import injection.Ctx
import story.Builder
import story.FactsOfTheWorld

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

class InternalConversation(val conversationSteps: Map<String, ConversationStep>) :IConversation {
  override val antagonistCanSpeak: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val protagonistCanChoose: Boolean
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val protagonist: IAgent
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val antagonist: IAgent
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

	init {
		val c = convo {
			startingStepKey = "start"
			step {
				key = "start"
				addLine("Välkommen!")
				addLine("Du är säkert trött sedan resan")
				addLine("- kom in och ta ett glas")
				addRoute(0, "")
			}
		}
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

	override fun build(): InternalConversation = InternalConversation(steps)
}

class ConversationStepBuilder() : Builder<ConversationStep> {
	var key = ""
	private val antagonistLines = mutableListOf<String>()
	private val conversationRoutes = mutableListOf<String>()
	private val routeTexts = mutableListOf<String>()

	fun addLine(line:String) {
		antagonistLines.add(line)
	}

	F

	fun addRoute(destinationKey: String) {
		/*
		Can we actually have any number of choices and routes?
		Why not, I mean, why not, really?
		 */
		conversationRoutes.add(destinationKey)
	}

	override fun build(): ConversationStep = ConversationStep(key, antagonistLines, conversationRoutes,routeTexts)
}

class ConversationStep(
    val key:String,
    val antagonistLines: Iterable<String>,
    val conversationRoutes: Iterable<String>,
    val routeTexts: Iterable<String> =
        setOf(
            "Ja",
            "Nej",
            "Är du dum eller?",
            "Sluta prata"))

class ConversationRoute(val positiveKey:String,
                        val positiveText:String = "Ja",
                        val negativeKey: String,
                        val negativeText: String = "Nej",
                        
                        ) {

}