package story

import com.lavaeater.kftw.data.IAgent

/**
 * Created by tommie on 2018-03-18.
 *
 * Keeps track of the global story state. Yay! Or? I dunno
 */

data class AgentFacts(val agent:IAgent,
                      val facts: MutableSet<Fact> = mutableSetOf(),
                      val stringValues: MutableMap<Fact, MutableSet<String>> = hashMapOf(),
                      val intValues: MutableMap<Fact, Int> = hashMapOf())

class AgentFactsManager {
  companion object {
    val agents = mutableSetOf<AgentFacts>()

    private fun filterAgentsOnFactsThatDoNotHaveString(predicate: Map<Fact, String>): Sequence<AgentFacts> {
      return predicate.map { filterAgentsOnFactNotHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

    private fun filterAgentsOnFactNotHavingString(fact: Fact, s:String): Sequence<AgentFacts> {
      return agents.asSequence().filter { !it.stringValues.containsKey(fact) || it.stringValues[fact]?.contains(s) == false }
    }

    fun filterAgentsOnFactsThatHaveString(predicate: Map<Fact, String>): Sequence<AgentFacts> {
      return predicate.map { filterAgentsOnFactHavingString(it.key, it.value) }
          .flatMap { it.asIterable() }
          .distinct()
          .asSequence()
    }

    fun filterAgentsOnFactHavingString(fact: Fact, s:String): Sequence<AgentFacts> {
      return agents.asSequence().filter { it.stringValues[fact]?.contains(s) == true }
    }

    fun filterAgentsOnIntValuesNotInRange(predicate: Map<Fact, IntRange>) : Sequence<AgentFacts> {
      return predicate.map { filterAgentsOnIntValueNotInRange(it.key, it.value) }.flatMap { it.asIterable() }.asSequence()
    }

    fun filterAgentsOnIntValueNotInRange(fact: Fact, range:IntRange): Sequence<AgentFacts> {
      return agents.asSequence().filter { !it.facts.contains(fact) || it.intValues[fact] !in range }
    }

    fun filterAgentsOnIntValues(predicate: Map<Fact, IntRange>) : Sequence<AgentFacts> {
      return predicate.map { filterAgentsOnIntValueInRange(it.key, it.value) }.flatMap { it.asIterable() }.asSequence()
    }

    fun filterAgentsOnIntValueInRange(fact: Fact, range:IntRange): Sequence<AgentFacts> {
      return agents.asSequence().filter { it.facts.contains(fact) && it.intValues[fact] in range }
    }

    fun getFactsFor(agent: IAgent): Set<Fact> {
      return if(agents.contains(agent)) agents.safeAgentFacts(agent).facts else emptySet()
    }

    fun hasFacts(agent: IAgent): Boolean {
      return agents.contains(agent)
    }

    fun has(agent: IAgent, fact: Fact): Boolean {
      return agents.has(agent, fact)
    }

    fun stateFact(agent:IAgent, fact:Fact) : AgentFacts {
      return agents.stateFact(agent, fact)
    }

    fun addStringListFactValue(agent: IAgent, fact: Fact, value: String) : AgentFacts {
      return agents.stateFactWithStringValue(agent, fact, value)
    }

    fun listStringValuesFor(agent: IAgent, fact: Fact) : Sequence<String> {
      return agents.listStringValues(agent, fact)
    }

    fun setStringValueFor(agent: IAgent, fact: Fact, v:String) {
      agents.stateFactWithStringValue(agent, fact, v)
    }

    fun getIntValueFor(agent:IAgent,fact: Fact): Int {
      return agents.getIntValue(agent, fact)
    }

    fun setIntValueFor(agent: IAgent, fact: Fact, v:Int) {
      agents.stateFactWithIntValue(agent, fact, v)
    }
  }
}

fun MutableSet<AgentFacts>.stateFactWithIntValue(agent: IAgent, fact: Fact, value:Int) {
  this.stateFact(agent, fact).intValues.set(fact, value)
}

fun <T> MutableMap<Fact, MutableSet<T>>.listValues(fact: Fact) :Sequence<T> {
  return if(this.containsKey(fact)) this[fact]!!.asSequence() else emptySequence()
}

fun <T> MutableMap<Fact, MutableSet<T>>.addValue(fact: Fact, value: T) {
  if(!this.containsKey(fact))
    this[fact] = mutableSetOf()
  this[fact]!!.add(value)
}

fun MutableSet<AgentFacts>.listStringValues(agent: IAgent, fact: Fact): Sequence<String> {
  return if(this.contains(agent)) this.safeAgentFacts(agent).stringValues.listValues(fact) else emptySequence()
}

fun MutableSet<AgentFacts>.getIntValue(agent: IAgent, fact: Fact): Int {
  return if(this.contains(agent)) this.safeAgentFacts(agent).intValues[fact]!! else -666
}

fun MutableSet<AgentFacts>.allFacts(agent: IAgent): Set<Fact> {
  return if(this.contains(agent)) this.safeAgentFacts(agent).facts else emptySet()
}

fun MutableSet<AgentFacts>.safeAgentFacts(agent:IAgent) : AgentFacts {
  if(!this.any { it.agent == agent })
    this.add(story.AgentFacts(agent))

  return this.first { it.agent == agent}
}

fun MutableSet<AgentFacts>.stateFactWithStringValue(agent:IAgent, fact:Fact, value:String) : AgentFacts {
  this.stateFact(agent,fact).stringValues.addValue(fact, value)
  return this.safeAgentFacts(agent)
}

fun MutableSet<AgentFacts>.stateFact(agent:IAgent, fact:Fact) : AgentFacts {
  this.safeAgentFacts(agent).facts.add(fact)
  return safeAgentFacts(agent)
}

fun MutableSet<AgentFacts>.contains(agent:IAgent) : Boolean {
  return this.any { it.agent == agent }
}

fun MutableSet<AgentFacts>.has(agent:IAgent, fact: Fact) : Boolean {
  return if(this.contains(agent)) this.safeAgentFacts(agent).facts.contains(fact) else false
}

fun IAgent.stateFact(fact:Fact) {
  AgentFactsManager.stateFact(this, fact)
}

fun IAgent.has(fact:Fact) : Boolean {
  return AgentFactsManager.has(this, fact)
}

enum class Fact {
  MetPlayer,
  UsedConversations,
  PlayerReputation
}