package world

import com.bladecoder.ink.runtime.Story

interface Consequence {
  var rule: Rule
  var facts: Set<Fact<*>>
  val consequenceType: ConsequenceType
}

interface ApplyConsequence : Consequence {
  fun applyConsequence()
}

interface ProcessInputConsequence : Consequence {
  fun <T> processInput(value: T)
}

interface RetrieveConsequence<T>:Consequence {
  fun retrieve() : T
}

class ConversationConsequence(val storyPath:String = "ink/dialog.ink.json",
                              override val consequenceType: ConsequenceType):RetrieveConsequence<Story> {
  override lateinit var rule: Rule
  override lateinit var facts: Set<Fact<*>>
  private val storyReader = InkLoader()
  override fun retrieve(): Story {
    return Story(storyReader.readStoryJson(storyPath))
  }
}

class Rule(val name: String,
           private val criteria: MutableCollection<Criterion> = mutableListOf(),
           val consequence: Consequence) {

  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<Fact<*>> = mutableSetOf()

  fun pass(facts: Set<Fact<*>>) : Boolean {

    if(facts.count() >= criteriaCount) {
      val res = facts.all {
        f -> criteria.first {
        c -> wildCardMatcher(c.key,f.key) }.isMatch(f) }
      if (res) {
        matchedFacts = facts
        return true
      }
    }
    return false
  }
}

fun wildCardMatcher(one:String, two:String) : Boolean {
  if(!one.contains('*') && !two.contains('*')) return one == two

  val os = one.substringBefore(".")
  val ts = two.substringBefore(".")

  return os == ts
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