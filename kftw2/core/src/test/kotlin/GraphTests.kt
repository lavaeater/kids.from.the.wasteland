import org.junit.After
import org.junit.Before
import org.junit.BeforeClass

class GraphTests {

	companion object {
		@JvmStatic
		@BeforeClass
		fun beforeClass() {
		}
	}

	@Before
	fun before() {
	}

	@After
	fun after() {
	}
}

interface IGraph {

}

interface INode {
	val id: Int
	val nodes
}

interface IRelation {
	val id: Int
}

interface ILabel {
	val value: String
}

interface IProperty<T> {
	val key: String
	var value: T
}

interface INumberProperty<T: Number> : IProperty<T>

abstract class Property<T> : IProperty<T>

abstract class NumberProperty<T: Number> : INumberProperty<T>

data class StringProperty(override val key: String, override var value: String) : Property<String>()
data class BooleanProperty(override val key: String, override var value: Boolean): Property<Boolean>()
data class IntegerProperty(override val key: String, override var value: Integer): NumberProperty<Integer>()
