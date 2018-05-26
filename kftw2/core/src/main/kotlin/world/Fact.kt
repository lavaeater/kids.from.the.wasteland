package world

class Fact<T>(val key: String, var value: T) {
  companion object {
    fun <T> createFact(key: String, value: T) : Fact<T> {
      return Fact(key, value)
    }

    fun <T> createListFact(key: String): Fact<MutableCollection<T>> {
      return Fact(key, mutableSetOf())
    }
  }
}