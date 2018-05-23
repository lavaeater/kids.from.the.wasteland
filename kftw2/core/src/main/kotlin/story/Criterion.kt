package story

class Criterion(factKey: String, private val matcher: (Fact<*>) -> Boolean, subKey: String = "") {
  val key = "$factKey.$subKey"
  fun isMatch(fact: Fact<*>):Boolean {
    return fact.key == key && matcher(fact)
  }

  companion object {
    fun booleanCriterion(factKey: String, checkFor: Boolean, subKey: String = "") : Criterion {
      return Criterion(factKey, { it.value == checkFor }, subKey)
    }

    fun <T> equalsCriterion(factKey: String, value: T, subKey: String = ""): Criterion {
      return Criterion(factKey, { it.value == value }, subKey)
    }

    fun rangeCriterion(factKey: String, range: IntRange, subKey: String = ""): Criterion {
      return Criterion(factKey, { it.value in range }, subKey)
    }

    fun containsCriterion(factKey: String, value: String, subKey: String = ""): Criterion {
      return Criterion(factKey, { (it.value as Collection<*>).contains(value) }, subKey)
    }

    fun context(context: String) : Criterion {
      return Criterion("Context", { fact -> fact.value == context })
    }
  }
}