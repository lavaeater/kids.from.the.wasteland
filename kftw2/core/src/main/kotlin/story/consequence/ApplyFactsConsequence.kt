package story.consequence

import injection.Ctx
import story.FactsOfTheWorld
import story.rule.Rule
import story.fact.IFact

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