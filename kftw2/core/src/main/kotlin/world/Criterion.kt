package world

import injection.Ctx

class Criterion(val key: String, private val matcher: (IFact<*>) -> Boolean) {
  fun isMatch(fact: IFact<*>):Boolean {
    return matcher(fact)
  }

  companion object {
    private val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

    fun booleanCriterion(key: String, checkFor: Boolean) : Criterion {
      return Criterion(key, { it.value == checkFor })
    }

    fun <T> equalsCriterion(key: String, value: T): Criterion {
      return Criterion(key, {
        it.value == value
      })
    }

    fun rangeCriterion(key: String, range: IntRange): Criterion {
      return Criterion(key, {
        it.value in range
      })
    }

    fun containsCriterion(key: String, value: String) :Criterion {
      return Criterion(key, {
        val factList = factsOfTheWorld.factListForKey(key)
        factList.contains(value)
      })
    }

    fun listContainsFact(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        val contextValue = factsOfTheWorld.stringForKey(contextKey)
           factsOfTheWorld.factListForKey(key).contains(contextValue)
      })
    }

    fun listDoesNotContainFact(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        val contextValue = factsOfTheWorld.stringForKey(contextKey)
        !factsOfTheWorld.factListForKey(key).contains(contextValue)
      })
    }

    fun context(context: String) : Criterion {
      return Criterion(Facts.Context, {
        fact -> fact.value == context
      })
    }

    fun notContainsCriterion(key: String, value: String): Criterion {
      return Criterion(key, {
        !factsOfTheWorld.factListForKey(key).contains(value)
      })
    }
  }
}