package world

class Criterion(factKey: String, private val matcher: (Fact<*>) -> Boolean, subKey: String = "") {
  val key = "$factKey.$subKey"
  fun isMatch(fact: Fact<*>):Boolean {
    return matcher(fact)
  }

  companion object {
    fun booleanCriterion(factKey: String, checkFor: Boolean, subKey: String = "") : Criterion {
      return Criterion(factKey, { it.value == checkFor }, subKey)
    }

    fun <T> equalsCriterion(factKey: String, value: T, subKey: String = ""): Criterion {
      return Criterion(factKey, {
        it.value == value
      }, subKey)
    }

    fun rangeCriterion(factKey: String, range: IntRange, subKey: String = ""): Criterion {
      return Criterion(factKey, {
        it.value in range
      }, subKey)
    }

    fun <T> containsCriterion(factKey: String, value: T, subKey: String ="") :Criterion {
      return Criterion(factKey, {
        FactsOfTheWorld.getFactList<T>(factKey, subKey).contains(value)
      }, subKey)
    }

    fun <T> factContainsFactValue(factKey:String, contextKey:String, subKey: String =""): Criterion {
      return Criterion(factKey, {
        var match = false
        val contextValue = FactsOfTheWorld.factValueOrNull<T>(contextKey)
        if (contextValue != null) {
          if(FactsOfTheWorld.getFactList<T>(factKey, subKey).contains(contextValue)) {
            match = true }

        }
         match
      }, subKey)
    }

    fun context(context: String) : Criterion {
      return Criterion(Facts.Context, {
        fact -> fact.value == context
      })
    }
  }
}