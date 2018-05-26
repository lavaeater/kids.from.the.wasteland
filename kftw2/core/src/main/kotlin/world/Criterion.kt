package world

class Criterion(val key: String, private val matcher: (Fact<*>) -> Boolean) {
  fun isMatch(fact: Fact<*>):Boolean {
    return matcher(fact)
  }

  companion object {
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

    fun <T> containsCriterion(key: String, value: T) :Criterion {
      return Criterion(key, {
        val factList = FactsOfTheWorld.getFactList<T>(key)
        factList.contains(value)
      })
    }

    fun <T> factContainsFactValue(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        var match = false
        val contextValue = FactsOfTheWorld.factValueOrNull<T>(contextKey)
        if (contextValue != null) {
          if(FactsOfTheWorld.getFactList<T>(key).contains(contextValue)) {
            match = true }

        }
         match
      })
    }
    fun <T> factDoesNotContainsFactValue(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        var match = false
        val contextValue = FactsOfTheWorld.factValueOrNull<T>(contextKey)
        if (contextValue != null) {
          if(!FactsOfTheWorld.getFactList<T>(key).contains(contextValue)) {
            match = true }

        }
        match
      })
    }

    fun context(context: String) : Criterion {
      return Criterion(Facts.Context, {
        fact -> fact.value == context
      })
    }

    fun <T> notContainsCriterion(key: String, value: T): Criterion {
      return Criterion(key, {
        val factList = FactsOfTheWorld.getFactList<T>(key)
        !factList.contains(value)
      })
    }
  }
}