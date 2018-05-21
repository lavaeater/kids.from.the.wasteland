package story

import com.lavaeater.kftw.data.IAgent

/**
 * Created by tommie on 2018-03-18.
 *
 * Keeps track of the global story state. Yay! Or? I dunno
 */

/*
Enums are bad because they are code.

The rules and fats of the world must be able to happen
without changing the games' code, obviously.

So we need a flexible idea of "concepts"

 */

data class AccentFats(val agent: IAgent,
                      val fats: MutableSet<Fat> = mutableSetOf(),
                      val stringValues: MutableMap<Fat, MutableSet<String>> = hashMapOf(),
                      val intValues: MutableMap<Fat, Int> = hashMapOf())

class FatsManager {
  companion object {
    private val agents = mutableSetOf<AccentFats>()

    private fun filterAgentsOnFactsThatDoNotHaveString(predicate: Map<Fat, String>): Sequence<AccentFats> {
      return predicate.map { filterAgentsOnFactNotHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

    fun filterAgentsOnFactNotHavingString(fat: Fat, s: String): Sequence<AccentFats> {
      return agents.asSequence().filter { !it.stringValues.containsKey(fat) || it.stringValues[fat]?.contains(s) == false }
    }

    fun filterAgentsOnFactsThatHaveString(predicate: Map<Fat, String>): Sequence<AccentFats> {
      return predicate.map { filterAgentsOnFactHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

	  fun filterAgentsOnHavingFact(fat:Fat):Sequence<AccentFats> {
		  return agents.agentsThatHave(fat)
	  }

	  fun filterAgentsOnHavingAllFacts(fats:Collection<Fat>) : Sequence<AccentFats> {
		  return agents.agentsThatHave(fats)
	  }

	  fun filterAgentsOnNotHavingFact(fat: Fat):Sequence<AccentFats> {
		  return agents.agentsThatDoNotHave(fat)
	  }

    fun filterAgentsOnFactHavingString(fat: Fat, s: String): Sequence<AccentFats> {
      return agents.asSequence().filter { it.stringValues[fat]?.contains(s) == true }
    }

    fun filterAgentsOnIntValuesNotInRange(predicate: Map<Fat, IntRange>): Sequence<AccentFats> {
      return predicate.map { filterAgentsOnIntValueNotInRange(it.key, it.value) }.flatMap { it.asIterable() }.asSequence()
    }

    fun filterAgentsOnIntValueNotInRange(fat: Fat, range: IntRange): Sequence<AccentFats> {
      return agents.asSequence().filter { !it.fats.contains(fat) || it.intValues[fat] !in range }
    }

    fun filterAgentsOnIntValues(predicate: Map<Fat, IntRange>): Sequence<AccentFats> {
      return predicate.map { filterAgentsOnIntValueInRange(it.key, it.value) }.flatMap { it.asIterable() }.asSequence()
    }

    fun filterAgentsOnIntValueInRange(fat: Fat, range: IntRange): Sequence<AccentFats> {
      return agents.asSequence().filter { it.fats.contains(fat) && it.intValues[fat] in range }
    }

    fun getFactsFor(agent: IAgent): Set<Fat> {
      return if (agents.contains(agent)) agents.safeAgentFacts(agent).fats else emptySet()
    }

    fun hasFacts(agent: IAgent): Boolean {
      return agents.contains(agent)
    }

    fun has(agent: IAgent, fat: Fat): Boolean {
      return agents.has(agent, fat)
    }

    fun stateFact(agent: IAgent, fat: Fat): AccentFats {
      return agents.stateFact(agent, fat)
    }

    fun addStringListFactValue(agent: IAgent, fat: Fat, value: String): AccentFats {
      return agents.stateFactWithStringValue(agent, fat, value)
    }

    fun listStringValuesFor(agent: IAgent, fat: Fat): Sequence<String> {
      return agents.listStringValues(agent, fat)
    }

    fun setStringValueFor(agent: IAgent, fat: Fat, v: String) {
      agents.stateFactWithStringValue(agent, fat, v)
    }

    fun setSingleValueFor(agent: IAgent, fat: Fat, v: String) {
      agents.stateFactWithSingleStringValue(agent, fat, v)
    }

    fun getIntValueFor(agent: IAgent, fat: Fat): Int? {
      return agents.getIntValue(agent, fat)
    }

    fun setIntValueFor(agent: IAgent, fat: Fat, v: Int) {
      agents.stateFactWithIntValue(agent, fat, v)
    }

	  fun addToIntFact(agent: IAgent, fat: Fat, value: Int): Int {
		  if(agents.getIntValue(agent, fat) == null) {
			  agents.stateFactWithIntValue(agent, fat, value)
			  return value
		  } else {
			  val newVal = agents.getIntValue(agent, fat)!! + value
			  agents.stateFactWithIntValue(agent,fat,newVal)
			  return newVal
		  }
	  }

	  fun addAgent(agent: IAgent) {
		  agents.safeAgentFacts(agent)
	  }
  }
}

fun MutableSet<AccentFats>.stateFactWithSingleStringValue(agent: IAgent, fat: Fat, v: String) {
  this.stateFact(agent, fat) //make sure fat is added to fats set
  val stringVals = this.safeAgentFacts(agent).stringValues

  if (!stringVals.containsKey(fat))
    stringVals[fat] = mutableSetOf()

  stringVals[fat]!!.clear()
  stringVals[fat]!!.add(v)
}

fun MutableSet<AccentFats>.stateFactWithIntValue(agent: IAgent, fat: Fat, value: Int) {
  this.stateFact(agent, fat).intValues.set(fat, value)
}

fun <T> MutableMap<Fat, MutableSet<T>>.listValues(fat: Fat): Sequence<T> {
  return if (this.containsKey(fat)) this[fat]!!.asSequence() else emptySequence()
}

fun <T> MutableMap<Fat, MutableSet<T>>.addValue(fat: Fat, value: T): MutableSet<T> {
  if (!this.containsKey(fat))
    this[fat] = mutableSetOf()
  this[fat]!!.add(value)
  return this[fat]!!
}

fun MutableSet<AccentFats>.listStringValues(agent: IAgent, fat: Fat): Sequence<String> {
  return if (this.contains(agent)) this.safeAgentFacts(agent).stringValues.listValues(fat) else emptySequence()
}

fun MutableSet<AccentFats>.getIntValue(agent: IAgent, fat: Fat): Int? {
  return if (this.contains(agent)) this.safeAgentFacts(agent).intValues[fat]!! else null
}

fun MutableSet<AccentFats>.allFacts(agent: IAgent): Set<Fat> {
  return if (this.contains(agent)) this.safeAgentFacts(agent).fats else emptySet()
}

fun MutableSet<AccentFats>.safeAgentFacts(agent: IAgent): AccentFats {
  if (!this.any { it.agent == agent })
    this.add(story.AccentFats(agent))

  return this.first { it.agent == agent }
}

fun MutableSet<AccentFats>.stateFactWithStringValue(agent: IAgent, fat: Fat, value: String): AccentFats {
  this.stateFact(agent, fat).stringValues.addValue(fat, value)
  return this.safeAgentFacts(agent)
}

fun MutableSet<AccentFats>.stateFact(agent: IAgent, fat: Fat): AccentFats {
  this.safeAgentFacts(agent).fats.add(fat)
  return safeAgentFacts(agent)
}

fun MutableSet<AccentFats>.contains(agent: IAgent): Boolean {
  return this.any { it.agent == agent }
}

fun MutableSet<AccentFats>.has(agent: IAgent, fat: Fat): Boolean {
  return if (this.contains(agent)) this.safeAgentFacts(agent).fats.contains(fat) else false
}

fun MutableSet<AccentFats>.agentsThatHave(fat:Fat) : Sequence<AccentFats> {
	return this.asSequence().filter { it.fats.contains(fat) }
}

fun MutableSet<AccentFats>.agentsThatHave(fats:Collection<Fat>) : Sequence<AccentFats> {
	return this.asSequence().filter { it.fats.containsAll(fats) }
}

//fun MutableSet<AccentFats>.agentsThatHaveNone(fats:Collection<Fat>) : Sequence<AccentFats> {
//	return this.asSequence().filter { it.fats.containsAll(fats) }
//}

fun MutableSet<AccentFats>.agentsThatDoNotHave(fat:Fat) : Sequence<AccentFats> {
	return this.asSequence().filter { !it.fats.contains(fat) }
}

fun IAgent.stateFact(fat: Fat) {
  FatsManager.stateFact(this, fat)
}

fun IAgent.stateFactWithValue(fat:Fat, value:String) {
  FatsManager.addStringListFactValue(this, fat, value)
}

fun IAgent.stateFactWithValue(fat:Fat, value: Int) {
  FatsManager.setIntValueFor(this, fat, value)
}

fun IAgent.stateFactWithSingle(fat:Fat, value:String) {
  FatsManager.setSingleValueFor(this, fat, value)
}

fun IAgent.has(fat: Fat): Boolean {
  return FatsManager.has(this, fat)
}

fun IAgent.stringsFor(fat: Fat): Set<String> {
  return FatsManager.listStringValuesFor(this, fat).toSet()
}

fun IAgent.stringFor(fat: Fat): String {
  if(this.has(fat)) {
    val list = FatsManager.listStringValuesFor(this, fat)
    return if(list.any()) list.first() else ""
  }
  return ""
}

fun IAgent.intFor(fat: Fat): Int? {
  return FatsManager.getIntValueFor(this, fat)
}

fun IAgent.addToIntFact(fat: Fat, value: Int) : Int {
	return FatsManager.addToIntFact(this, fat, value)
}

fun IAgent.subtractFromIntFact(fat: Fat, value:Int):Int {
	return FatsManager.addToIntFact(this, fat, -value)
}

enum class Fat {
  MetPlayer,
	MetPlayerParents,
  UsedConversations,
  PlayerHate,
  Name
}