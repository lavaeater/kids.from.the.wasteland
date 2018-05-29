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

interface IFact<T> {
  val key:String
  var value: T
}

interface IListFact<T>: IFact<MutableSet<T>> {
  fun contains(value: T):Boolean
}

class StringFact(override val key: String, override var value: String) :IFact<String>

class IntFact(override val key: String, override var value: Int) : IFact<Int>

class BooleanFact(override val key: String, override var value: Boolean) :IFact<Boolean>

class ListFact(override val key: String, override var value: MutableSet<String> = mutableSetOf()) : IListFact<String> {
  override fun contains(value: String): Boolean {
    return value.contains(value)
  }
}