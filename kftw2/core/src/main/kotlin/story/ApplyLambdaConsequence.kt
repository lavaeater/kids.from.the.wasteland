package story

class ApplyLambdaConsequence(private val applier:(Rule, Set<IFact<*>>)->Unit): ApplyConsequence {
  override lateinit var rule: Rule
  override lateinit var facts: Set<IFact<*>>
  override val consequenceType = ConsequenceType.ApplyLambdaConsequence
  override fun applyConsequence() {
    applier(rule, facts)
  }
}

class ApplyLambdaConsequenceBuilder:Builder<ApplyLambdaConsequence> {
  var applier: (Rule, Set<IFact<*>>) -> Unit = {_,_ -> }

  override fun build(): ApplyLambdaConsequence {
    return ApplyLambdaConsequence(applier)
  }
}