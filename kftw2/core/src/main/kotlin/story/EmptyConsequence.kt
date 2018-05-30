package story

class EmptyConsequence : Consequence {
  override lateinit var rule: Rule

  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.Empty
}