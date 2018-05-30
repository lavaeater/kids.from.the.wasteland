package story

import story.fact.*
import story.rule.Rule

class FactsOfTheWorld(private val preferences: com.badlogic.gdx.Preferences, clearFacts: Boolean = false) {
  val npcNames = mapOf(
      1 to "Ulrica Wikren",
      2 to "Kim Dinh Thi",
      3 to "Andreas Lindblad",
      4 to "Babak Varfan"
  )
	init {
		if(clearFacts)
			clearAllFacts()
	}

  fun factForKey(key: String): IFact<*>? {
    if(preferences.contains(key))
      return factForKey(key, preferences.get()[key]!!)

    return null
  }

  fun factForKey(key: String, value:Any): IFact<*> {

    if(value is Boolean)
      return BooleanFact(key, value)

    if(value is Int)
      return IntFact(key, value)

    if(value is String) {
      if (value.contains("List:"))
        return ListFact(key, value.replace("List:", "").split("|").toMutableSet())
      else
        return StringFact(key, value)
    }
    throw IllegalArgumentException("BLAGH")
  }

  fun factsForKeys(keys: Set<String>) : Sequence<IFact<*>> {
    //A key can be "VisitedCities" or "VisitedCities.Europe" or something...

    val facts = preferences.get().filterKeys { keys.contains(it) }.map { factForKey(it.key, it.value!!) }.asSequence()
    return facts
  }

  ///Checks the rule, with supplied Context. Any keys in Context
  ///that exists in world facts are filtered out
  ///so a fact only exists once. Yay
  private fun checkRule(rule: Rule) :Boolean {
    val factsToCheck = factsForKeys(rule.keys)
        .toSet()
    return rule.pass(factsToCheck)
  }

  fun rulesThatPass(rules:Set<Rule>) : List<Rule> {
    return rules.filter { checkRule(it)}
        .sortedByDescending { it.criteriaCount }
  }

  fun stateBoolFact(key:String, value: Boolean) {
    val fact = BooleanFact(key, value)
    storeBooleanFact(fact)
  }

  fun stateStringFact(key: String, value: String) {
    val fact = StringFact(key, value)
    storeStringFact(fact)
  }

  fun storeStringFact(fact: StringFact) {
    preferences.putString(fact.key, fact.value)
  }

  fun clearStringFact(key: String) {
    preferences.putString(key, "")
  }

  fun stateIntFact(key: String, value: Int) {
    val fact = IntFact(key, value)
    storeIntFact(fact)
  }

  fun addToIntFact(key: String, value: Int) {
    val factValue = getIntValue(key)
    storeIntFact(IntFact(key, factValue + value))
  }

  fun subtractFromIntFact(key: String, value: Int) {
    val factValue = getIntValue(key)
    storeIntFact(IntFact(key, factValue - value))
  }

  fun addToList(key: String, value: String) {
    val fact = ensureListFact(key)
    fact.value.add(value)
    saveListFact(fact)
  }

  private fun saveListFact(fact: ListFact) {
    preferences.putString(fact.key, fact.value.serializeToString())
  }

  fun removeFromList(key: String, value: String) {
    val fact = ensureListFact(key)
    fact.value.remove(value)
    saveListFact(fact)
  }

  fun storeBooleanFact(fact: BooleanFact) {
      preferences.putBoolean(fact.key, fact.value)
  }

  fun getIntValue(key: String) : Int {
    return preferences.getInteger(key, 0)
  }

  private fun storeIntFact(fact: IntFact) {
    preferences.putInteger(fact.key, fact.value)
  }

  private fun ensureListFact(key: String): ListFact {
    return if(preferences.contains(key)) ListFact(key, preferences.getString(key).toMutableSet()) else ListFact(key, mutableSetOf())
  }

  fun contains(key: String): Boolean {
    return preferences.contains(key)
  }

  fun clearAllFacts() {
    preferences.clear()
  }

  fun setupInitialFacts() {

  }

  fun getFactList(key: String): ListFact {
    return ensureListFact(key)
  }

  fun getStringFact(key: String): StringFact {
    return if(preferences.contains(key)) StringFact(key, preferences.getString(key)) else StringFact(key, "")
  }

	fun getIntFact(key:String): IntFact {
		return if (preferences.contains(key)) IntFact(key, preferences.getInteger(key)) else IntFact(key, 0)
	}

	fun getBooleanFact(key:String): BooleanFact {
		return if (preferences.contains(key)) BooleanFact(key, preferences.getBoolean(key)) else BooleanFact(key, false)
	}

  fun stringForKey(key: String): String {
    return getStringFact(key).value
  }

	fun save() {
		preferences.flush()
	}
}

enum class FactTypes {
  String,
  Int,
  Boolean,
  List
}
