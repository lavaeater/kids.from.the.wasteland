package story


/**
 * A "story" in the game.
 *
 * A story, conceptually, is a collection of steps.
 * A step is simply a rule that must be fulfilled.
 *
 * A rule is, as we know, a collection of criteria
 *
 * It would be nice with a criteria->rule->story
 * DSL and also a human-readable syntax for writing
 * them.
 *
 * Anyways, the facts of a criteria and or rule will
 * be set somewhere else. So, a story cannot base its
 * rules on stuff that isn't set "somewhere else"
 *
 * A lot of the things that could be a basis for a story
 * isn't implemented yet, such as "Player has visited place
 * A" or "Player has found 5 magical crystals"
 *
 * Actually, finding stuff must be stored in the facts
 * of the world. At least "unique" stuff. So if an item has
 * a "special" flag, it's string based key (or integer,
 * but then we need integer lists oh my goad) must be stored
 * in some list of items that is used to keep track of it.
 *
 * We must also have some way of triggering everything
 * that happens in the game.
 *
 * So, the player moves about - triggers updates about
 * areas and such.
 *
 * Bah, we refactor it as we need.
 *
 * Anyway, we need a hook or message that is sent to some
 * class that handles messages
 *
 */
class Story(val name:String, val rules: Set<Rule>)

interface Builder<out T> {
	fun build(): T
}

class StoryBuilder: Builder<Story> {
	var name = ""
	private val rules = mutableSetOf<Rule>()

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

object TEST {
	val testStory = story {
		name = "FindTheCheerLeader"
		rule {
			name = "HasCheerLeaderBeenFound"
			containsCriterion(Facts.NpcsPlayerHasMet, "Cheerleader")
			conversation {
				storyPath ="Someinkfile"
			}
		}
	}
}
