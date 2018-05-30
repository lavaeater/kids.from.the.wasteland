package story.consequence

import story.rule.Rule
import story.fact.IFact

class ApplyLambdaConsequence(private val applier:(Rule, Set<IFact<*>>)->Unit): ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
  override fun applyConsequence() {
    applier(rule, facts)
  }
}