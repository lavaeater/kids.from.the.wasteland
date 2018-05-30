package story

import com.bladecoder.ink.runtime.Story
import com.lavaeater.kftw.GameSettings
import injection.Ctx

interface Consequence {
  var rule: Rule
  var facts: Set<IFact<*>>
  val consequenceType: ConsequenceType
}

class EmptyConsequence :Consequence {
  override lateinit var rule: Rule

  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.Empty
}

interface ApplyConsequence : Consequence {
  fun applyConsequence()
}

class ApplyLambdaConsequence(private val applier:(Rule, Set<IFact<*>>)->Unit):ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
  override fun applyConsequence() {
    applier(rule, facts)
  }

}

class ApplyFactsConsequence(val factsMap: Map<String, (IFact<*>)->Unit>) : ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyFactsConsequence

  override fun applyConsequence() {
    //We could use injection to inject the global facts everyehwere...
    val facts = Ctx.context.inject<FactsOfTheWorld>().factsForKeys(factsMap.keys)
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
	private val basePath by lazy { Ctx.context.inject<GameSettings>().assetBaseDir}
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ConversationLoader
  private val storyReader = InkLoader()
  override fun retrieve(): Story {
    return Story(storyReader.readStoryJson("$basePath/$storyPath"))
  }
}

class RuleBasedConversationConsequence(val convo:RuleBasedConversation) :RetrieveConsequence<RuleBasedConversation> {
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ConversationLoader
  private val storyReader = InkLoader()
  override fun retrieve(): RuleBasedConversation {
    return convo
  }
}

class Rule(val name: String,
           private val criteria: MutableCollection<Criterion> = mutableListOf(),
           var consequence: Consequence = EmptyConsequence()) {

  val keys : Set<String> get() = criteria.map { it.key }.distinct().toSet()
  val criteriaCount = criteria.count()

  var matchedFacts: Set<IFact<*>> = mutableSetOf()

//  fun pass(facts: Set<Fact<*>>) : Boolean {
//
//    if(facts.count() >= criteriaCount) {
//      val res = facts.all {
//        f -> criteria.first { it.key == f.key }.isMatch(f) }
//      if (res) {
//        matchedFacts = facts
//        consequence.facts = matchedFacts
//        consequence.rule = this
//        return true
//      }
//    }
//    return false
//  }

	fun pass(facts: Set<IFact<*>>) : Boolean {

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

class RulesOfTheWorld {
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

	fun setupRules() {
		addRule(Rule("FirstMeetingWithNPC", mutableListOf(
				Criterion.context(Contexts.MetNpc)),
				ConversationConsequence("conversations/beamon_memory.ink.json")))
	}

	val rules : Set<Rule> get() { return rulesOfTheWorld.values.toSet() }
}