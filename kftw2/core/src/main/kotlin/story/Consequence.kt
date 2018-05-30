package story

interface Consequence {
  var rule: Rule
  var facts: Set<IFact<*>>
	val consequenceType: ConsequenceType
}