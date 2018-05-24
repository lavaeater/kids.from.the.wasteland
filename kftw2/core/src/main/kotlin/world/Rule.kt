package world

import com.bladecoder.ink.runtime.Story

interface Consequence {
  var rule: Rule
  var facts: Set<Fact<*>>
  val consequenceType: ConsequenceType
}

class EmptyConsequence :Consequence {
  override lateinit var rule: Rule

  override lateinit var facts: Set<Fact<*>>
  override val consequenceType = ConsequenceType.Empty
}

interface ApplyConsequence : Consequence {
  fun applyConsequence()
}

class ApplyLambdaConsequence(private val applier:(Rule, Set<Fact<*>>)->Unit):ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<Fact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
  override fun applyConsequence() {
    applier(rule, facts)
  }

}

class ApplyFactsConsequence(val factsMap: Map<String, (Fact<*>)->Unit>) : ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<Fact<*>>
  override val consequenceType = ConsequenceType.ApplyFactsConsequence

  override fun applyConsequence() {
    //We could use injection to inject the global facts everyehwere...
    val facts = FactsOfTheWorld.factsForKeys(factsMap.keys)
    for (fact in facts) {
      factsMap[fact.key]?.invoke(fact)
    }
  }
}

interface ProcessInputConsequence : Consequence {
  fun <T> processInput(value: T)
}

interface RetrieveConsequence<T>:Consequence {
  fun retrieve() : T
}

class ConversationConsequence(private val storyPath:String = "ink/dialog.ink.json"):RetrieveConsequence<Story> {
  override lateinit var rule: Rule
  override lateinit var facts: Set<Fact<*>>
  override val consequenceType = ConsequenceType.ConversationLoader
  private val storyReader = InkLoader()
  override fun retrieve(): Story {
    return Story(storyReader.readStoryJson(storyPath))
  }
}

class Rule(val name: String,
           private val criteria: MutableCollection<Criterion> = mutableListOf(),
           var consequence: Consequence = EmptyConsequence()) {

  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<Fact<*>> = mutableSetOf()

  fun pass(facts: Set<Fact<*>>) : Boolean {

    if(facts.count() >= criteriaCount) {
      val res = facts.all {
        f -> criteria.first { it.key == f.key }.isMatch(f) }
      if (res) {
        matchedFacts = facts
        consequence.facts = matchedFacts
        consequence.rule = this
        return true
      }
    }
    return false
  }
}

//fun wildCardMatcher(one:String, two:String) : Boolean {
//  if(!one.contains('*') && !two.contains('*')) return one == two
//
//  val os = one.substringBefore(".")
//  val ts = two.substringBefore(".")
//
//  return os == ts
//}

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

criterion