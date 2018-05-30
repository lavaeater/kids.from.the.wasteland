package story.consequence

import story.rule.Rule
import story.fact.IFact

interface Consequence {
  var rule: Rule
  var facts: Set<IFact<*>>
	val consequenceType: ConsequenceType
}