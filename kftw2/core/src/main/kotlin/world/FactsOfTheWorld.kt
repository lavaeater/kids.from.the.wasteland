package world

class FactsOfTheWorld(private val preferences: com.badlogic.gdx.Preferences) {

  val valueTypes = mapOf<String, FactTypes>()

  val npcNames = mapOf(
      1 to "Ulrica Wikren",
      2 to "Kim Dinh Thi",
      3 to "Andreas Lindblad",
      4 to "Babak Varfan"
  )

  fun factForKey(key: String): IFact<*>? {
    if(preferences.contains(key))
      return factForKey(key, preferences.get()[key]!!)

    return null
  }

  fun factForKey(key: String, value:Any): IFact<*> {
    when(valueTypes[key]) {
      FactTypes.Boolean -> return BooleanFact(key, value as Boolean)
      FactTypes.Int -> return IntFact(key, value as Int)
      FactTypes.String -> return StringFact(key, value as String)
      FactTypes.List -> return ListFact(key, (value as String).split("|").toMutableSet())
    }
    throw IllegalArgumentException("No fact with key $key is registered in the type list.")
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
    ensureListFact(key).value.add(value)
  }

  fun addValueToFactList(key: String, value: String) {
    val fact = ensureListFact(key)
    fact.value.add(value)
    saveListFact(fact)
  }

  private fun saveListFact(fact: ListFact) {
    preferences.putString(fact.key, fact.value.joinToString { "|" })
  }

  fun removeValueFromFactList(key: String, value: String) {
    val fact = ensureListFact(key)
    fact.value.remove(value)
    saveListFact(fact)
  }

  private fun ensureBooleanFact(key: String): BooleanFact {
    if(!preferences.contains(key)) {
      if(valueTypes.containsKey(key) && valueTypes[key] != FactTypes.Boolean)
        throw IllegalArgumentException("Cannot state a Boolean fact for a key already declared as ${valueTypes[key]}")

      preferences.putBoolean(key, false)
    }

    return BooleanFact(key, preferences.getBoolean(key))
  }

  private fun ensureStringFact(key: String): StringFact {
    if(!preferences.contains(key)) {
      if(valueTypes.containsKey(key) && valueTypes[key] != FactTypes.String)
        throw IllegalArgumentException("Cannot state a String fact for a key already declared as ${valueTypes[key]}")

      preferences.putString(key, "")
    }

    return StringFact(key, preferences.getString(key))
  }

  fun getIntValue(key: String) : Int {
    return preferences.getInteger(key, 0)
  }

  private fun ensureIntFact(key: String): IntFact {
    if(!preferences.contains(key)) {
      if(valueTypes.containsKey(key) && valueTypes[key] != FactTypes.Int)
        throw IllegalArgumentException("Cannot state an int fact for a key already declared as ${valueTypes[key]}")

      preferences.putInteger(key, 0)
    }

    return IntFact(key, preferences.getInteger(key))
  }

  private fun ensureListFact(key: String): ListFact {
    if(!preferences.contains(key)) {
      if(valueTypes.containsKey(key) && valueTypes[key] != FactTypes.List)
        throw IllegalArgumentException("Cannot state a List fact for a key already declared as ${valueTypes[key]}")

      preferences.putString(key, "")
    }

    return ListFact(key, preferences.getString(key).split("|").toMutableSet())
  }

  fun contains(key: String): Boolean {
    return preferences.contains(key)
  }

  fun clearAllFacts() {
    preferences.clear()
  }

  fun setupInitialFacts() {
    stateIntFact("MetNumberOfNpcs", 0)
  }

  fun factListForKey(key: String): ListFact {
    return ensureListFact(key)
  }

  fun stringFactForKey(key: String): StringFact {
    return ensureStringFact(key)
  }

  fun stringForKey(key: String): String {
    return stringFactForKey(key).value
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
