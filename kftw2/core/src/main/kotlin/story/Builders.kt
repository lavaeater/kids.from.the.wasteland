package story

import com.lavaeater.kftw.GameSettings
import injection.Ctx
import story.consequence.Consequence
import story.consequence.ConversationConsequence
import story.consequence.EmptyConsequence
import story.consequence.SimpleConsequence
import story.conversation.InkLoader
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
	private val basePath by lazy { Ctx.context.inject<GameSettings>().assetBaseDir}
	private fun getPath(path:String): String {
		return "$basePath/$path" }
	var afterConversation: (story: com.bladecoder.ink.runtime.Story) -> Unit = {}
	var beforeConversation: (story: com.bladecoder.ink.runtime.Story)-> Unit = {}

	private lateinit var story: com.bladecoder.ink.runtime.Story

	fun inkStory(storyPath: String, block: com.bladecoder.ink.runtime.Story.() -> Unit) {
		story = com.bladecoder.ink.runtime.Story(InkLoader().readStoryJson(getPath(storyPath))).apply(block)
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
	private val rules = mutableListOf<Rule>()
	private var consequence: Consequence = EmptyConsequence()

	fun rule(block: RuleBuilder.() -> Unit) {
		rules.add(RuleBuilder().apply(block).build())
	}

	fun consequence(block: ConsequenceBuilder.() -> Unit) {
		consequence = ConsequenceBuilder().apply(block).build()
	}

	override fun build() : Story = Story(name, rules, consequence)
}

class CriteriaBuilder:Builder<Criterion> {
	var key = ""
	var matcher: (IFact<*>) -> Boolean = { false }
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

