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
	fun removeProperty(node: Node, property: String)
	fun addOrUpdateProperty(node:Node, property:String, value:Any)
	fun removeLabel(node: Node, label:String)
	fun addLabel(node:Node, label:String)
	fun addNode(node:Node)
	fun removeNode(node:Node)
	fun removeNode(id: Int)
	fun addRelation(from:Node, to:Node, relation: String)
	fun removeRelation(from:Node, to: Node, relation: String)
}

class GraphEngine : Graph {
	var nodeIdCounter = 0;
	val nodes = mutableMapOf<Int,Node>()
	val labels = mutableMapOf<String, MutableSet<Node>>()
	val properties = mutableMapOf<String, MutableSet<Pair<Node, Any>>>()
	val relations = mutableMapOf<String, MutableSet<Pair<Node,Node>>>()

	override fun removeProperty(node: Node, property: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun addOrUpdateProperty(node: Node, property: String, value: Any) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun removeLabel(node: Node, label: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun addLabel(node: Node, label: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


	override fun addRelation(from: Node, to: Node, relation: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun removeRelation(from: Node, to: Node, relation: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


	override fun addNode(node: Node) {
		nodes[node.id] = node

	}

	override fun removeNode(node: Node) {
		removeNode(node.id)
		removeAllLabels(node)
		removeAllRelations(node)
		removeAllProperties(node)
	}

	private fun removeAllLabels(node: Node) {
		val ls = labels.filter { it.value.contains(node) }.keys
		for (label in ls)
			removeLabel(node, label)
	}

	override fun removeNode(id: Int) {
		nodes.remove(id)
	}
}

interface Node {
	val id: Int
	val labels: MutableSet<String>
	val properties: MutableSet<Prop>
}

interface Relation {
	val id: Int
	val from: Node
	val to: Node
	val label: Label
}

interface Label {
	val value: String
}

interface Prop {
	val key: String
}

interface Property<T: Any> : Prop {
	override val key: String
	var value: T
}

interface NumberProperty<T: Number> : Property<T>

data class StringProperty(override val key: String, override var value: String) : Property<String>
data class BooleanProperty(override val key: String, override var value: Boolean): Property<Boolean>
data class IntegerProperty(override val key: String, override var value: Integer): NumberProperty<Integer>
