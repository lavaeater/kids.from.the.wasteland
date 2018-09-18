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

interface Graph {

}

interface Node {
	val id: Int
	val labels: MutableSet<Label>
}

interface Relation {
	val id: Int
}

interface Label {
	val value: String
}

interface Property<T> {
	val key: String
	var value: T
}

interface NumberProperty<T: Number> : Property<T>

data class StringProperty(override val key: String, override var value: String) : Property<String>
data class BooleanProperty(override val key: String, override var value: Boolean): Property<Boolean>
data class IntegerProperty(override val key: String, override var value: Integer): NumberProperty<Integer>
