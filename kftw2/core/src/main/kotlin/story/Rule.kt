package story

class Rule(val name:String, private val criteria: MutableCollection<Criterion> = mutableListOf(), private val consequence: (Rule, Set<Fact<*>>) -> Unit = { _, _ -> }) {
  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<Fact<*>> = mutableSetOf()

  fun pass(facts: Set<Fact<*>>) : Boolean {

    if(facts.all { f -> criteria.filter { c -> c.key == f.key }.all { c -> c.isMatch(f) } }) {
      matchedFacts = facts
      return true
    }
    return false
  }

  fun applyConsequence() {
    consequence(this, matchedFacts)
  }
}

class RulesOfTheWorld {
  companion object {
    private val rulesOfTheWorld = mutableMapOf<String, Rule>()

    fun addRule(rule:Rule) {
      rulesOfTheWorld[rule.name] = rule
    }

    fun removeRuleByName(name:String) {
        rulesOfTheWorld.remove(name)
    }

    fun findRuleByName(name:String) :Rule? {
      return rulesOfTheWorld[name]
    }

    val rules : Set<Rule> get() { return rulesOfTheWorld.values.toSet() }

  }
}