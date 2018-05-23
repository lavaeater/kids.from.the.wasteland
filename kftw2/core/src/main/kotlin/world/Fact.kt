package world

class Fact<T>(factKey: String, var value: T, subKey: String = "") {
  val key = "$factKey.$subKey"
  companion object {
    fun <T> createFact(factKey: String, value: T, subKey: String = "") : Fact<T> {
      return Fact(factKey, value, subKey)
    }

    fun <T> createListFact(factKey: String, subKey: String = ""): Fact<MutableCollection<T>> {
      return Fact(factKey, mutableSetOf(), subKey)
    }
  }
}