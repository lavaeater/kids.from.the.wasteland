package story

import injection.Ctx

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