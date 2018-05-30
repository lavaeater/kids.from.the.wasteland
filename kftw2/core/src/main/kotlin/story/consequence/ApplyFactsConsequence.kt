package story.consequence

import injection.Ctx
import story.FactsOfTheWorld
import story.fact.IFact
import story.rule.Rule

/**
 * This class can be used in a rule as a setting of facts consequence,
 * we need a sweet builder for it.
 */
class ApplyFactsConsequence(val factsMap: Map<String, (IFact<*>)->Unit>) : ApplyConsequence {
  override fun apply() {
  }

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