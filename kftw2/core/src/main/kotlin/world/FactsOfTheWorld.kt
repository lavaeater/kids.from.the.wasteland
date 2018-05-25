package world


class FactsOfTheWorld {

  /*
 They are for everything
    */
  companion object {

    val npcNames = mapOf(
        1 to "Ulrica Wikren",
        2 to "Kim Dinh Thi",
        3 to "Andreas Lindblad",
        4 to "Babak Varfan"
    )
    /*
    Statically accessible from the entire game.
    Contains Map of Facts?

    We need a system that manages quickly changing concepts in the game
    world, and these concepts can then affect:

    * probabilities of encounters
    * story advancement
    * skip fudging GUIDS for agents, dull and boring, we need human-readable names
    * for actor-related fats!
    *
    * Whenever something happens, a broadcast system can send a message (already used)
    * to say that a "concept" has happened, like a player meeting an npc
    * or whatevs.
    *
    * This global state can contain "count of heirlooms" - or whatevs.
    *
    * We  might need a sweet sweet dsl to build rules, but that should be easy:
     */
    private val factsOfTheWorld: MutableMap<String, Fact<*>> = mutableMapOf()

    fun factsForKeys(keys: Set<String>) : Sequence<Fact<*>> {
      //A key can be "VisitedCities" or "VisitedCities.Europe" or something...

      val facts = factsOfTheWorld.filterKeys { keys.contains(it) }.map { it.value }.asSequence()

      return facts
    }

    fun <T> factValueOrNull(key: String):T? {
      return factsOfTheWorld.valueOrNull(key)
    }

    fun <T> getFactList(key:String): Set<T> {
      return factsOfTheWorld.valuesOrEmpty(key)
    }

    ///Checks the rule, with supplied Context. Any keys in Context
    ///that exists in world facts are filtered out
    ///so a fact only exists once. Yay
    fun checkRule(rule: Rule) :Boolean {
      val factsToCheck = factsForKeys(rule.keys)
          .toSet()
      return rule.pass(factsToCheck)
    }

    fun rulesThatPass(rules:Set<Rule>) : List<Rule> {
      return rules.filter { checkRule(it)}
		      .sortedByDescending { it.criteriaCount }
    }

    fun stateBoolFact(key:String, value: Boolean) {
      ensureBooleanFact(key).value = value
    }

    fun stateStringFact(key: String, value: String) {
      ensureStringFact(key).value = value
    }

    fun clearStringFact(key: String) {
      ensureStringFact(key).value = ""
    }

    fun stateIntFact(key: String, value: Int) {
      ensureIntFact(key).value = value
    }

    fun addToIntFact(key: String, value: Int) {
      ensureIntFact(key).value+=value
    }

    fun subtractFromIntFact(key: String, value: Int) {
      ensureIntFact(key).value-=value
    }

    fun addStringToList(key: String, value: String) {
      ensureStringListFact(key).value.add(value)
    }

    //With this we can add whatever we want to the facts
    fun stateAnyFact(fact: Fact<*>) {
      factsOfTheWorld[fact.key] = fact
    }

    fun <T> addValueToFactList(key: String, value: T) {
      ensureFactList<T>(key).value.add(value)
    }

    fun <T> ensureFactList(key: String) : Fact<MutableCollection<T>> {
      if(!factsOfTheWorld.containsKey(key)) {
        factsOfTheWorld[key] = Fact.createListFact<T>(key)
      }
      return (factsOfTheWorld[key]!! as Fact<MutableCollection<T>>)
    }

    fun <T> removeValueFromFactList(key: String, value: T) {
      ensureFactList<T>(key).value.remove(value)
    }

    fun removeString(key: String, value: String) {
      ensureStringListFact(key).value.remove(value)
    }

    private fun ensureBooleanFact(key: String): Fact<Boolean> {
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(key, false)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<Boolean>
    }

    private fun ensureStringFact(key: String): Fact<String> {
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(key, "")
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<String>
    }

    fun getIntValue(key: String) : Int {
      return factsOfTheWorld.valueOrNull<Int>(key) ?: 0
    }

    private fun ensureIntFact(key: String): Fact<Int> {
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(key, 0)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<Int>
    }

    private fun ensureStringListFact(key: String): Fact<MutableCollection<String>> {
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createListFact<String>(key)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<MutableCollection<String>>
    }

    fun contains(key: String): Boolean {
      return factsOfTheWorld.containsKey(key)
    }

    fun clearAllFacts() {
      factsOfTheWorld.clear()
    }
  }
}

fun <T> MutableMap<String, Fact<*>>.valueOrNull(key: String): T? {
  return if(this.containsKey(key)) (this[key] as Fact<T>).value else null
}

fun <T> MutableMap<String, Fact<*>>.valuesOrEmpty(key:String): Set<T> {
  return if(this.containsKey(key)) (this[key] as Fact<MutableCollection<T>>).value.toSet() else emptySet()
}

fun MutableMap<String, Fact<*>>.factForKey(key: String):Fact<*>? {
  return this[key]
}

