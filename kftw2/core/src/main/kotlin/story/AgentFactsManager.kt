package story

import com.lavaeater.kftw.data.IAgent

/**
 * Created by tommie on 2018-03-18.
 *
 * Keeps track of the global story state. Yay! Or? I dunno
 */

class AgentFactsManager {
  companion object {
    val agentFacts: MutableMap<IAgent, MutableSet<Fact>> = hashMapOf()
    val agentStringListValues = mutableMapOf<IAgent, MutableMap<Fact, StringListFactValueStore>>()
    val agentStringValues = mutableMapOf<IAgent, MutableMap<Fact, FactValueStore<String>>>()
    val agentIntegerValues = mutableMapOf<IAgent, MutableMap<Fact, FactValueStore<Int>>>()

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

    fun listStringValuesFor(agent: IAgent, fact: Fact) : List<String> {
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

interface FactValueStorage<T> {
  fun storeValue(v: T)
  fun retrieveValue() : T
  fun listValues() : List<T>
  fun hasValue(v: T): Boolean
}

abstract class ListFactValueStore<T> : FactValueStorage<T> {
  val values = mutableSetOf<T>()
  override fun storeValue(v: T) {
    values.add(v)
  }

  override abstract fun retrieveValue(): T

  override fun listValues(): List<T> {
    return values.toList()
  }

  override fun hasValue(v: T): Boolean {
    return values.contains(v)
  }
}

class FactValueStore<T>(private var value :T): FactValueStorage<T> {
  override fun storeValue(v: T) {
    this.value = v
  }

  override fun retrieveValue(): T {
    return value
  }

  override fun listValues(): List<T> {
    return listOf(value)
  }

  override fun hasValue(v: T): Boolean {
    return value == v
  }
}

class StringListFactValueStore() : ListFactValueStore<String>() {
  override fun retrieveValue(): String {
    return values.joinToString(separator = ", ")
  }

  override fun hasValue(v: String): Boolean {
    return values.contains(v)
  }

  override fun storeValue(v: String) {
    values.add(v)
  }

  override fun listValues(): List<String> {
    return values.toList()
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

