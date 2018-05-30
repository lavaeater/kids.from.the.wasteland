package story.consequence

import story.fact.IFact
import story.rule.Rule

class ApplyLambdaConsequence(private val applier:(Rule, Set<IFact<*>>)->Unit): ApplyConsequence {
  override fun apply() {

  }

  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
  override fun applyConsequence() {
    applier(rule, facts)
  }
}