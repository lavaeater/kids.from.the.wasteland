package story

import story.consequence.Consequence
import story.consequence.ConversationConsequence
import story.consequence.EmptyConsequence
import story.consequence.SimpleConsequence
import story.conversation.*
import story.fact.IFact
import story.rule.Criterion
import story.rule.Rule

class ConsequenceBuilder: Builder<Consequence> {
	var apply: () -> Unit = {}

  override fun build(): SimpleConsequence {
    return SimpleConsequence(apply)
  }
}

class ConversationConsequenceBuilder : Builder<ConversationConsequence> {

	var afterConversation: (story: com.bladecoder.ink.runtime.Story) -> Unit = {}
	var beforeConversation: (story: com.bladecoder.ink.runtime.Story)-> Unit = {}

	private lateinit var story: com.bladecoder.ink.runtime.Story

	fun inkStory(storyPath: String, block: com.bladecoder.ink.runtime.Story.() -> Unit) {
		story = com.bladecoder.ink.runtime.Story(InkLoader().readStoryJson(storyPath)).apply(block)
	}

	override fun build(): ConversationConsequence {
		return ConversationConsequence(story,afterConversation, beforeConversation)
	}
}

interface Builder<out T> {
	fun build(): T
}

class StoryBuilder: Builder<Story> {
	var name = ""
	var initializer : () -> Unit = {}
	private val rules = mutableListOf<Rule>()
	private var consequence: Consequence = EmptyConsequence()

	fun rule(block: RuleBuilder.() -> Unit) {
		rules.add(RuleBuilder().apply(block).build())
	}

	fun consequence(block: ConsequenceBuilder.() -> Unit) {
		consequence = ConsequenceBuilder().apply(block).build()
	}

	override fun build() : Story = Story(name, rules, consequence, initializer)
}

class CriteriaBuilder:Builder<Criterion> {
	var key = ""
	private var matcher: (IFact<*>) -> Boolean = { false }
	/*
	val key: String, private val matcher: (IFact<*>) -> Boolean
	 */

	override fun build(): Criterion {
		return Criterion(key, matcher)
	}

}

class RuleBuilder:Builder<Rule> {
	var name = ""
	private val criteria = mutableSetOf<Criterion>()
	var consequence: Consequence? = null

	fun criterion(block: CriteriaBuilder.() -> Unit) {
		criteria.add(CriteriaBuilder().apply(block).build())
	}

	fun booleanCriteria(key: String, checkFor:Boolean) {
		criteria.add(Criterion.booleanCriterion(key, checkFor))
	}

	fun <T> equalsCriterion(key: String, value: T) {
		criteria.add(Criterion.equalsCriterion(key, value))
	}
	fun rangeCriterion(key: String, range: IntRange) {
		criteria.add(Criterion.rangeCriterion(key, range))
	}

	fun containsCriterion(key: String, value: String) {
		criteria.add(Criterion.containsCriterion(key, value))
	}
	fun listContainsFact(key:String, contextKey:String) {
		criteria.add(Criterion.listContainsFact(key, contextKey))
	}

	fun listDoesNotContainFact(key:String, contextKey:String) {
		criteria.add(Criterion.listDoesNotContainFact(key, contextKey))
	}

	fun context(context: String) {
		criteria.add(Criterion.context(context))
	}

	fun notContainsCriterion(key: String, value: String) {
		criteria.add(Criterion.notContainsCriterion(key, value))
	}

	fun consequence(block: ConsequenceBuilder.() -> Unit) {
		consequence = ConsequenceBuilder().apply(block).build()
	}

	fun conversation(block: ConversationConsequenceBuilder.() -> Unit) {
		consequence = ConversationConsequenceBuilder().apply(block).build()
	}

	override fun build(): Rule {
		if(consequence == null)
			throw IllegalStateException("You must define a consequence for a rule")
		return Rule(name, criteria, consequence!!)
	}

}

fun story(block: StoryBuilder.() -> Unit) = StoryBuilder().apply(block).build()

fun convo(block: InternalConversationBuilder.() -> Unit) = InternalConversationBuilder().apply(block).build()

class InternalConversationBuilder : Builder<InternalConversation> {
	var startingStepKey = "start"
	val steps = mutableMapOf<String, ConversationStep>()
	var onChoice: (ConversationRoute)-> Unit = {}

	fun step(block: ConversationStepBuilder.() -> Unit) {
		val c = ConversationStepBuilder().apply(block).build()
		steps[c.key] = c
	}

	override fun build(): InternalConversation {
		if(!steps.containsKey("Abort"))
			steps["Abort"] = ConversationStep("Abort", listOf("Tack för pratet"), emptyList())

		return InternalConversation(startingStepKey, steps, onChoice =  onChoice)
	}

	fun trader(topKey: String) {
		step {
			key ="trade"
			addLine("Goddag, kära kund!")
			addLine("Vad kan ni vara intresserad av idag, tro?")
			neutralContinue("weapons", "Jag letar efter vapen")
			neutralContinue("armor", "Jag behöver skydd")
			neutralContinue("gadgets", "Vad har du för prylar?")
			neutralContinue("selling", "Jag har prylar att sälja")
			negativeContinue(topKey, "Sluta handla")
		}
		//Weapons menu
		step {
			key ="weapons"
			addLine("VAPEN!")
			negativeContinue("trade", "Jag vill köpa något annat")
		}
		//Armor menu
		step {
			key ="armor"
			addLine("Skydd!")
			negativeContinue("trade", "Jag vill köpa något annat")
		}
		//Gadget menu
		step {
			key ="gadgets"
			addLine("PRYLAR!")
			negativeContinue("trade", "Jag vill köpa något annat")
		}
		//Selling menu
		step {
			key ="selling"
			addLine("Jag kan ta en titt på det du har med dig")
			addLine("och ge dig ett erbjudande.")
			negativeContinue("trade", "Jag vill nog inte sälja något ändå")
		}
	}

	fun dungeon() {
		step {
			key ="dungeon"
			addLine("Du står framför ingången till en bortglömd bas.")
			addLine("Vad gömmer basen för hemligheter från forntiden?")
			positiveContinue("enter", "Äventyr! Jag går in!")
			negativeContinue("enter", "Jag får en dålig känsla av det här... men jag går in")
			neutralExit("Abort", "Jag vänder")
		}
	}

	fun bulletin(topKey: String, bulletinKey: String = "bulletin") {
		step {
			key = bulletinKey
			addLine("I alla städer finns det en anslagstavla")
			addLine("med saker att läsa.")
			addLine("Vad vill du läsa om?")
			neutralContinue("local_news", "Lokala nyheter")
			neutralContinue("world_news", "Nyheter från hela ödemarken")
			neutralContinue("quests", "Uppdrag och annonser")
			negativeContinue(topKey, "Sluta läsa")
		}
		//Weapons menu
		val topText = "Njae, läs något annat"
		step {
			key ="local_news"
			addLine("Nyheter")
			negativeContinue(bulletinKey, topText)
		}
		//Armor menu
		step {
			key ="world_news"
			addLine("Ödemarksnytt!")
			negativeContinue(bulletinKey, topText)
		}
		//Gadget menu
		step {
			key ="quests"
			addLine("Dårar och äventyrare sökes!")
			negativeContinue(bulletinKey, topText)
		}
	}
}

class ConversationStepBuilder() : Builder<ConversationStep> {
	var key = ""
	private val antagonistLines = mutableListOf<String>()
	private val conversationRoutes = mutableListOf<ConversationRoute>()

	fun addLine(line:String) {
		antagonistLines.add(line)
	}

	fun neutralContinue(key: String, text: String = "Neutral") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Neutral))
	}

	fun positiveContinue(key: String, text: String ="Ja") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Positive))
	}

	fun negativeContinue(key:String, text: String = "Nej") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Negative))
	}

	fun rudeContinue(key:String, text: String = "Far åt helvete!") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Rude))
	}

	fun neutralExit(key: String, text: String = "Neutral") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Neutral, RouteType.Exit))
	}

	fun positiveExit(key: String, text: String ="Ja") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Positive, RouteType.Exit))
	}

	fun negativeExit(key:String, text: String = "Nej") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Negative, RouteType.Exit))
	}

	fun rudeExit(key:String, text: String = "Far åt helvete!") {
		conversationRoutes.add(ConversationRoute(key, text, RouteEmotion.Rude, RouteType.Exit))
	}

	override fun build(): ConversationStep = ConversationStep(key, antagonistLines, conversationRoutes)
}

