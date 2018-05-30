package story

import story.consequence.ApplyLambdaConsequence
import story.consequence.Consequence
import story.consequence.ConversationConsequence
import story.fact.IFact
import story.rule.Criterion
import story.rule.Rule

class ApplyLambdaConsequenceBuilder: Builder<ApplyLambdaConsequence> {
  var applier: (Rule, Set<IFact<*>>) -> Unit = { _, _ -> }

  override fun build(): ApplyLambdaConsequence {
    return ApplyLambdaConsequence(applier)
  }
}

class ConversationConsequenceBuilder : Builder<ConversationConsequence> {
	var storyPath: String = ""
	override fun build(): ConversationConsequence {
		return ConversationConsequence(storyPath)
	}

}


interface Builder<out T> {
	fun build(): T
}

class StoryBuilder: Builder<Story> {
	var name = ""
	private val rules = mutableListOf<Rule>()

	fun rule(block: RuleBuilder.() -> Unit) {
		rules.add(RuleBuilder().apply(block).build())
	}

	override fun build() : Story = Story(name, rules)
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

	fun applyLambdaConsequence(block: ApplyLambdaConsequenceBuilder.() -> Unit) {
		consequence = ApplyLambdaConsequenceBuilder().apply(block).build()
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

