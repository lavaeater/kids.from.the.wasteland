package story

class ConceptManager {

  /*
  This is for conversations, but can they be
  for anything?


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
    val factsOfTheWorld: MutableMap<String, Fact<*>> = mutableMapOf()

//    fun filterOnStringVal(s:String) {
//      factsOfTheWorld.asSequence().filter {
//        it is StringFact && it.value == s ||
//            it is StringListFact && it.value.contains(s) }
//    }

    fun <T> factValueOrNull(factKey: String, subKey: String = ""):T? {
      return factsOfTheWorld.valueOrNull(factKey, subKey)
    }

    fun <T> getFactList(factKey: String, subKey: String=""): Set<T> {
      return factsOfTheWorld.valuesOrEmpty(factKey, subKey)
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

fun key(factKey: String, subKey: String = ""):String {
  return "$factKey.$subKey"
}

//abstract class FactBase(factKey: String, subKey:String = "") {
//  val key = "$factKey.$subKey"
//}
//First, the data for some global fact - or local for an agent... how?
class Fact<T>(factKey: String, var value: T, subKey: String = "") {
  val key = "$factKey.$subKey"
  companion object {
    fun <T> createFact(factKey: String, value: T, subKey: String = "") : Fact<T> {
      return Fact(factKey, value, subKey)
    }

    fun <T> createListFact(factKey: String, subKey: String = ""):Fact<MutableCollection<T>> {
      return Fact(factKey, mutableSetOf(), subKey)
    }
  }
}
//abstract class ListFact<T>(factKey: String, subKey:String = "", override var value: MutableCollection<T> = mutableSetOf()): Fact<MutableCollection<T>>(factKey, subKey, value)

//class StringFact(factKey: String, subKey: String, override var value: String = ""): Fact<String>(factKey, value, subKey)
//class BooleanFact(factKey: String, subKey: String, override var value: Boolean = false): Fact<Boolean>(factKey, subKey, value)
//class IntFact(factKey: String,subKey: String, override var value: Int = 0): Fact<Int>(factKey, subKey, value)
//class StringListFact(factKey: String, subKey: String) : ListFact<String>(factKey = factKey, subKey = subKey)

class Criterion<E>(factKey: String, private val matcher: (Fact<E>) -> Boolean, subKey: String = "") {
  val key = "$factKey.$subKey"
  fun isMatch(fact: Fact<E>):Boolean {
    return fact.key == key && matcher(fact)
  }

  companion object {
    fun booleanCriterion(factKey: String, checkFor: Boolean, subKey: String = "") : Criterion<Boolean> {
      return Criterion(factKey, { it.value == checkFor }, subKey)
    }

    fun <T> equalsCriterion(factKey: String, value: T, subKey: String = ""): Criterion<T> {
      return Criterion(factKey, { it.value == value}, subKey)
    }

    fun rangeCriterion(factKey: String, range: IntRange, subKey: String = ""): Criterion<Int> {
      return Criterion(factKey, { it.value in range}, subKey)
    }

    fun containsCriterion(factKey: String, value: String, subKey: String = ""):Criterion<MutableCollection<String>> {
      return Criterion(factKey, { it.value.contains(value)}, subKey)
    }
  }
}

//class BooleanCriterion(factKey: String, subKey: String, isIt:Boolean): Criterion<Boolean, Fact<Boolean>>(factKey, subKey, matcher = { it.value == isIt })
//
//class StringCriterion(factKey: String, subKey: String, matcher: (Fact<String>) -> Boolean): Criterion<String>(factKey, subKey, matcher)
//
//class IntCriterion(factKey: String, subKey: String, matcher: (Fact<Int>) -> Boolean): Criterion<Int>(factKey, subKey, matcher)
//
//class ListCriterion(factKey: String, subKey: String, matcher: (StringListFact) -> Boolean): Criterion<StringListFact>(factKey, subKey, matcher)
