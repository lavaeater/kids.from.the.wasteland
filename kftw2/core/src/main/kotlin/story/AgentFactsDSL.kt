package story

class AgentFactsDSL {

	val criteria = criteria {
		having = setOf(Fact.MetPlayer, Fact.MetPlayerParents)
		notHaving = setOf(Fact.PlayerHate)
		havingInRange = mapOf(Fact.PlayerHate to 12..39)
		notHavingInRange = mapOf(Fact.PlayerHate to 32..34)
		havingValue = mapOf(Fact.Name to "Lars")
		havingValue = mapOf(Fact.MetPlayer to 12)
		notHavingValue = mapOf(Fact.MetPlayer to 12)
		notHavingValue = mapOf(Fact.Name to "Lars")
	}
}

/*

Agent facts used as criteria? So, an agent fact
can be built to match something else.
 */