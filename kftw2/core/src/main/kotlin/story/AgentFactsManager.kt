package story

import com.lavaeater.kftw.data.IAgent

/**
 * Created by tommie on 2018-03-18.
 *
 * Keeps track of the global story state. Yay! Or? I dunno
 */

data class AgentFacts(val agent:IAgent, val facts: Set<Fact>, val stringValues: Map<Fact, Set<String>>, val intValues: Map<Fact, Int>)

data class AgentPredicate(val mustHaveFacts: Collection<Fact>? = null,
                              val excludingFacts: Collection<Fact>? = null,
                              val factIntValuesInRange: Map<Fact, IntRange>? = null,
                              val factIntValuesNotInRange: Map<Fact, IntRange>? = null,
                              val factHasString: Map<Fact, String>? = null,
                              val factHasNotString: Map<Fact, String>? = null)

class AgentFactsManager {
  companion object {
    val agents = mutableSetOf<AgentFacts>()
    val agentFacts: MutableMap<IAgent, MutableSet<Fact>> = hashMapOf()
    val agentStringListValues = mutableMapOf<IAgent, MutableMap<Fact, StringListFactValueStore>>()
    val agentStringValues = mutableMapOf<IAgent, MutableMap<Fact, FactValueStore<String>>>()
    val agentIntegerValues = mutableMapOf<IAgent, MutableMap<Fact, FactValueStore<Int>>>()




    fun filterOnAnyyything(predicate: AgentPredicate) : Sequence<IAgent> {
      return agentFacts.filterAgents {
        it.value.containsAll(if(predicate.mustHaveFacts != null) predicate.mustHaveFacts else emptyList<Fact>()) &&
            !it.value.any { predicate.excludingFacts?.contains(it) == true }
      }.toList()
          .union(
              if(predicate.factIntValuesInRange != null) filterAgentsOnIntValues(predicate.factIntValuesInRange).toList() else emptyList()
          )
          .intersect(
              if(predicate.factIntValuesNotInRange != null) filterAgentsOnIntValuesNotInRange(predicate.factIntValuesNotInRange).toList() else emptyList()
          )
          .intersect(
              if(predicate.factHasString != null) filterAgentsOnFactsThatHaveString(predicate.factHasString).toList() else emptyList()
          )
          .intersect(
              if(predicate.factHasNotString != null) filterAgentsOnFactsThatDoNotHaveString(predicate.factHasNotString).toList() else emptyList()
          ).asSequence()
    }

    private fun filterAgentsOnFactsThatDoNotHaveString(predicate: Map<Fact, String>): Sequence<IAgent> {
      return predicate.map { filterAgentsOnFactNotHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

    private fun filterAgentsOnFactNotHavingString(fact: Fact, s:String): Sequence<IAgent> {
      return agentStringValues.filterValues { !it.containsKey(fact) || !it[fact]!!.hasValue(s) }
          .map { it.key }
          .union(
              agentStringListValues
                  .filterValues { !it.containsKey(fact) || it[fact]!!.hasValue(s) }
                  .map { it.key }
          ).asSequence()
    }

    private fun filterAgentsOnFactsThatHaveString(predicate: Map<Fact, String>): Sequence<IAgent> {
      return predicate.map { filterAgentsOnFactHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

    private fun filterAgentsOnFactHavingString(fact: Fact, s:String): Sequence<IAgent> {
      return agentStringValues.filterValues { it.containsKey(fact) && it[fact]!!.hasValue(s) }
          .map { it.key }
          .union(
              agentStringListValues
                  .filterValues { it.containsKey(fact) && it[fact]!!.hasValue(s) }
                  .map { it.key }
          ).asSequence()
    }

    fun filterAgentsOnIntValuesNotInRange(predicate: Map<Fact, IntRange>) : Sequence<IAgent> {
      return predicate.map { filterAgentsOnIntValueNotInRange(it.key, it.value) }.flatMap { sequence -> sequence.asIterable() }
          .distinct()
          .asSequence()
    }

    fun filterAgentsOnIntValueNotInRange(fact: Fact, range:IntRange): Sequence<IAgent> {
      return agentIntegerValues.agentsWithFactNotInRange(fact, range)
    }

    fun filterAgentsOnIntValues(predicate: Map<Fact, IntRange>) : Sequence<IAgent> {
      return predicate.map { filterAgentsOnIntValueInRange(it.key, it.value) }.flatMap { sequence -> sequence.asIterable() }
          .distinct()
          .asSequence()
    }

    fun filterAgentsOnIntValueInRange(fact: Fact, range:IntRange): Sequence<IAgent> {
      return agentIntegerValues.agentsWithFactInRange(fact, range)
    }

    fun getFactsFor(agent: IAgent): Set<Fact> {
      return if (hasFacts(agent)) agentFacts[agent]!! else emptySet()
    }

    fun hasFacts(agent: IAgent): Boolean {
      return agentFacts.containsKey(agent)
    }

    fun has(agent: IAgent, fact: Fact): Boolean {
      return hasFacts(agent) && agentFacts[agent]!!.contains(fact)
    }

    fun state(agent:IAgent, fact:Fact) {
      ensure(agent).add(fact)
    }

    fun ensure(agent:IAgent):MutableSet<Fact> {
      if(!agentFacts.containsKey(agent)) {
        agentFacts[agent] = mutableSetOf()
        agentStringListValues[agent] = hashMapOf()
        agentStringValues[agent] = hashMapOf()
        agentIntegerValues[agent] = hashMapOf()
      }
      return agentFacts[agent]!!
    }

    fun addStringListFactValue(agent: IAgent, fact: Fact, value: String) {
      ensure(agent).add(fact)
      ensureStringList(agent, fact).storeValue(value)
    }

    private fun ensureStringList(agent: IAgent, fact: Fact) : StringListFactValueStore {
      if(!agentStringListValues[agent]!!.containsKey(fact)) {
        agentStringListValues[agent]!![fact] = StringListFactValueStore()
      }
      return agentStringListValues[agent]!![fact]!!
    }

    fun listStringValuesFor(agent: IAgent, fact: Fact) : Sequence<String> {
      return ensureStringList(agent, fact).listValues()
    }

    fun getStringValueFor(agent:IAgent,fact: Fact): String {
      return ensureStringStore(agent, fact).retrieveValue()
    }

    fun setStringValueFor(agent: IAgent, fact: Fact, v:String) {
      ensureStringStore(agent, fact).storeValue(v)
    }

    fun getIntValueFor(agent:IAgent,fact: Fact): Int {
      return ensureIntegerStore(agent, fact).retrieveValue()
    }

    private fun ensureIntegerStore(agent: IAgent, fact: Fact): FactValueStorage<Int> {
      if(!agentIntegerValues[agent]!!.containsKey(fact)) {
        agentIntegerValues[agent]!![fact] = FactValueStore(0)
      }
      return agentIntegerValues[agent]!![fact]!!    }

    fun setIntValueFor(agent: IAgent, fact: Fact, v:Int) {
      ensureIntegerStore(agent, fact).storeValue(v)
    }

    private fun ensureStringStore(agent: IAgent, fact: Fact): FactValueStorage<String> {
      if(!agentStringValues[agent]!!.containsKey(fact)) {
        agentStringValues[agent]!![fact] = FactValueStore("")
      }
      return agentStringValues[agent]!![fact]!!

    }
  }
}

fun Map<IAgent, MutableSet<Fact>>

fun Map<IAgent, Map<Fact, FactValueStore<Int>>>.agentsWithFactNotInRange(fact: Fact, range:IntRange):Sequence<IAgent> {
  return this.filterValues { !it.containsKey(fact) || !it[fact]!!.valueInRange(range)}.keys.asSequence()
}

fun Map<IAgent, Map<Fact, FactValueStore<Int>>>.agentsWithFactInRange(fact: Fact, range:IntRange):Sequence<IAgent> {
  return this.filterValues { it.containsKey(fact) && it[fact]!!.valueInRange(range)}.keys.asSequence()
}

inline fun Map<IAgent, MutableSet<Fact>>.filterAgents(predicate: (Map.Entry<IAgent, Set<Fact>>) -> Boolean) : Sequence<IAgent> {
    return this.filter(predicate).map { it.key}.asSequence()
}
fun Map<IAgent, MutableSet<Fact>>.withFacts(facts: Collection<Fact>) : Sequence<IAgent> {
  return this.asSequence().filter { it.value.containsAll(facts) }.map { it.key}
}

interface FactValueStorage<T:Comparable<T>> {
  fun storeValue(v: T)
  fun retrieveValue() : T
  fun hasValue(v: T): Boolean
  fun valueInRange(r: ClosedRange<T>) : Boolean
}

interface ListableFactValueStorage<T:Comparable<T>> : FactValueStorage<T> {
  fun listValues() : Sequence<T>
}

abstract class ListFactValueStore<T:Comparable<T>> : ListableFactValueStorage<T> {
  val values = mutableSetOf<T>()
  override fun storeValue(v: T) {
    values.add(v)
  }

  override abstract fun retrieveValue(): T

  override fun listValues(): Sequence<T> {
    return values.asSequence()
  }

  override fun hasValue(v: T): Boolean {
    return values.contains(v)
  }
}

class FactValueStore<T:Comparable<T>>(private var value :T): FactValueStorage<T> {
  override fun valueInRange(r: ClosedRange<T>) : Boolean {
    return value in r
  }

  override fun storeValue(v: T) {
    this.value = v
  }

  override fun retrieveValue(): T {
    return value
  }

  override fun hasValue(v: T): Boolean {
    return value == v
  }
}

class StringListFactValueStore() : ListFactValueStore<String>() {
  override fun valueInRange(r: ClosedRange<String>): Boolean {
    return values.any { it in r }
  }

  override fun retrieveValue(): String {
    return values.joinToString(separator = ", ")
  }

  override fun hasValue(v: String): Boolean {
    return values.contains(v)
  }

  override fun storeValue(v: String) {
    values.add(v)
  }

  override fun listValues(): Sequence<String> {
    return values.asSequence()
  }
}

fun IAgent.has(fact:Fact):Boolean {
  return AgentFactsManager.has(this, fact)
}

fun IAgent.state(fact: Fact) {
  AgentFactsManager.state(this, fact)
}

fun IAgent.addStringListFactValue(fact:Fact, value:String) {
  AgentFactsManager.addStringListFactValue(this, fact, value)
}

enum class Fact {
  MetPlayer,
  UsedConversations,
  PlayerReputation
}

