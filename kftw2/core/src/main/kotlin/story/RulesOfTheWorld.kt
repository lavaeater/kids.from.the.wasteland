package story

class RulesOfTheWorld {
	private val rulesOfTheWorld = mutableMapOf<String, Rule>()

	fun addRule(rule: Rule) {
		rulesOfTheWorld[rule.name] = rule
	}

	fun removeRuleByName(name:String) {
		rulesOfTheWorld.remove(name)
	}

	fun findRuleByName(name:String) : Rule? {
		return rulesOfTheWorld[name]
	}

	fun setupRules() {
		addRule(Rule("FirstMeetingWithNPC", mutableListOf(
				Criterion.context(Contexts.MetNpc)),
				ConversationConsequence("conversations/beamon_memory.ink.json")))
	}

	val rules : Set<Rule> get() { return rulesOfTheWorld.values.toSet() }
}