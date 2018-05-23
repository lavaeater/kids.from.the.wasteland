package world

class FactsOfTheWorld {

  /*
 They are for everything
    */
  companion object {
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

      val facts = factsOfTheWorld.filterKeys { keys.any { k -> wildCardMatcher(k, it) } }.map { it.value }.asSequence()

      return facts
    }

    fun <T> factValueOrNull(factKey: String, subKey: String = ""):T? {
      return factsOfTheWorld.valueOrNull(factKey, subKey)
    }

    fun <T> getFactList(factKey: String, subKey: String=""): Set<T> {
      return factsOfTheWorld.valuesOrEmpty(factKey, subKey)
    }

    ///Checks the rule, with supplied context. Any keys in context
    ///that exists in world facts are filtered out
    ///so a fact only exists once. Yay
    fun checkRule(rule: Rule, context: Set<Fact<*>>) :Boolean {
      val factsToCheck = factsForKeys(rule.keys)
//          .filter { rule.keys.contains(it.key) }
          .toSet()
          .union(context.filter {
            rule.keys.any { k -> wildCardMatcher(k, it.key) }
          })

      return rule.pass(factsToCheck)
    }

    fun rulesThatPass(rules:Set<Rule>, context: String): List<Rule> {
      return rulesThatPass(rules, setOf(Fact("Context", context)))
    }

    fun rulesThatPass(rules:Set<Rule>, context: Set<Fact<*>> = emptySet()) : List<Rule> {
      return rules.filter { checkRule(it, context)}
		      .sortedByDescending { it.criteriaCount }
    }

    fun stateBoolFact(factKey: String, value: Boolean, subKey: String = "") {
      ensureBooleanFact(factKey, subKey).value = value
    }

    fun stateStringFact(factKey: String, value: String, subKey: String = "") {
      ensureStringFact(factKey, subKey).value = value
    }

    fun stateIntFact(factKey: String, value: Int, subKey: String = "") {
      ensureIntFact(factKey, subKey).value = value
    }

    fun addToIntFact(factKey: String, value: Int, subKey: String = "") {
      ensureIntFact(factKey, subKey).value+=value
    }

    fun subtractFromIntFact(factKey: String, value: Int, subKey: String = "") {
      ensureIntFact(factKey, subKey).value-=value
    }

    fun addStringToList(factKey: String, value: String, subKey: String = "") {
      ensureStringListFact(factKey, subKey).value.add(value)
    }

    //With this we can add whatever we want to the facts
    fun stateAnyFact(fact: Fact<*>) {
      factsOfTheWorld[fact.key] = fact
    }

    fun <T> addValueToFactList(factKey: String, value: T, subKey: String ="") {
      val key = key(factKey, subKey)
      ensureFactList<T>(factKey, subKey).value.add(value)
    }

    fun <T> ensureFactList(factKey: String, subKey: String = "") : Fact<MutableCollection<T>> {
      val key = key(factKey, subKey)
      if(!factsOfTheWorld.containsKey(key)) {
        factsOfTheWorld[key(factKey,subKey)] = Fact.createListFact<T>(factKey, subKey)
      }
      return (factsOfTheWorld[key]!! as Fact<MutableCollection<T>>)
    }

    fun <T> removeValueFromFactList(factKey: String, value: T, subKey: String) {
      ensureFactList<T>(factKey, subKey).value.remove(value)
    }

    fun removeString(factKey: String, value: String, subKey: String = "") {
      ensureStringListFact(factKey, subKey).value.remove(value)
    }

    private fun ensureBooleanFact(factKey: String, subKey: String = ""): Fact<Boolean> {
      val key = key(factKey, subKey)
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(factKey, false, subKey)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<Boolean>
    }

    private fun ensureStringFact(factKey: String, subKey: String = ""): Fact<String> {
      val key = key(factKey, subKey)
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(factKey, "", subKey)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<String>
    }

    private fun ensureIntFact(factKey: String, subKey: String =""): Fact<Int> {
      val key = key(factKey, subKey)
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createFact(factKey, 0, subKey)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<Int>
    }

    private fun ensureStringListFact(factKey: String, subKey: String = ""): Fact<MutableCollection<String>> {
      val key = key(factKey, subKey)
      if(!factsOfTheWorld.containsKey(key)) {
        val f = Fact.createListFact<String>(factKey, subKey)
        factsOfTheWorld[key] = f
      }
      return factsOfTheWorld[key] as Fact<MutableCollection<String>>
    }
  }
}

fun <T> MutableMap<String, Fact<*>>.valueOrNull(factKey: String, subKey: String = ""): T? {
  return if(this.containsKey(factKey, subKey)) (this[key(factKey, subKey)] as Fact<T>).value else null
}

fun <T> MutableMap<String, Fact<*>>.valuesOrEmpty(factKey: String, subKey: String = ""): Set<T> {
  return if(this.containsKey(factKey, subKey)) (this[key(factKey, subKey)] as Fact<MutableCollection<T>>).value.toSet() else emptySet()
}

fun MutableMap<String, Fact<*>>.containsKey(factKey: String, subKey: String = "") :Boolean {
  return this.containsKey(key(factKey, subKey))
}

fun MutableMap<String, Fact<*>>.factForKey(factKey: String, subKey: String = ""):Fact<*>? {
  return this[key(factKey, subKey)]
}

fun key(factKey: String, subKey: String = ""):String {
  return "$factKey.$subKey"
}

