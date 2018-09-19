import com.sun.xml.internal.fastinfoset.util.StringArray
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import kotlin.test.Test

class GraphTests {
	companion object {

		val graphEngine = GraphEngine()

		@JvmStatic
		@BeforeClass
		fun beforeClass() {

		}
	}

	@Test
	fun addAndFindRelations() {
		/*
		What can we do, what can we do?

		All relations are directional, so adding a "has met" relation means
		we have to add it to both parties... or? Can we have different types?

		Relations will have properties, later.
		 */
		val playerNode = graphEngine.newNode()
		val npcNode = graphEngine.newNode()
		graphEngine.addBiDirectionalRelation(playerNode, npcNode, "has met")
		val otherNodes = mutableMapOf<INode, INode>()
		for(i in 0..10)
			otherNodes.put(graphEngine.newNode(), graphEngine.newNode())

		val relations = arrayOf("has seen", "hates", "killed", "loves", "wants to find")

		var index = 0

		for((first, second) in otherNodes) {
				graphEngine.addRelation(first, second, relations[index % 5])
				graphEngine.addRelation(second, first, relations[(index + 1) % 5])
			index++
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
	fun removeProperty(node: INode, property: String)
	fun addOrUpdateProperty(node:INode, property:String, value:Any)
	fun removeLabel(node: INode, label:String)
	fun addLabel(node:INode, label:String)
	fun newNode() : INode
	fun removeNode(node:INode)
	fun removeNode(id: Int)
	fun addRelation(from:INode, to:INode, relation: String)
	fun addBiDirectionalRelation(first:INode, second:INode, relation: String)
	fun removeRelation(from:INode, to: INode, relation: String)
}



class GraphEngine : Graph {
	override fun addBiDirectionalRelation(first: INode, second: INode, relation: String) {
		addRelation(first, second, relation)
		addRelation(second, first, relation)
	}

	var idCounter = 0;
	val nodes = mutableMapOf<Int,INode>()
	val labels = mutableMapOf<String, MutableSet<INode>>()
	//val properties = mutableMapOf<String, MutableSet<Pair<INode, Any>>>()

	val relations = mutableSetOf<IRelation>()

	fun consumeNextId() : Int {
		return idCounter++;
	}

	override fun removeProperty(node: INode, property: String) {
	}

	override fun addOrUpdateProperty(node: INode, property: String, value: Any) {
	}

	override fun removeLabel(node: INode, label: String) {
		labels.safeRemove(label, node)
	}

	override fun addLabel(node: INode, label: String) {
		labels.safeAdd(label, node)
	}

	override fun addRelation(from: INode, to: INode, relation: String) {
		relations.add(Relation(from, to, relation))
	}

	override fun removeRelation(from: INode, to: INode, relation: String) {
		val relationToRemove = relations.firstOrNull { it.from == from && it.to == to && it.name == relation }
		if(relationToRemove != null)
			removeRelation(relationToRemove)
	}


	override fun newNode() : INode {
		val node = Node(consumeNextId())
		nodes[node.id] = node
		return node
	}

	override fun removeNode(node: INode) {
		removeNode(node.id)
		removeAllLabels(node)
		removeAllRelations(node)
	}

	private fun removeAllProperties(node: INode) {
	}

	private fun removeAllRelations(node: INode) {
		for(relation in relations.filter { it.from == node || it.to == node })
			removeRelation(relation)
	}

	private fun removeRelation(relation: IRelation) {
		relations.remove(relation)
	}

	private fun removeAllLabels(node: INode) {
		val ls = labels.filter { it.value.contains(node) }.keys
		for (label in ls)
			removeLabel(node, label)
	}

	override fun removeNode(id: Int) {
		nodes.remove(id)
	}
}



interface INode {
	val id: Int
}

interface IRelation {
	val from: INode
	val to: INode
	val name: String
}

data class Relation(override val from: INode, override val to: INode, override val name: String) : IRelation
data class Node(override val id: Int) :INode

interface Label {
	val value: String
}

interface Prop {
	val key: String
}

interface IProperty<T: Any> : Prop {
	override val key: String
	var value: T
}

fun <K,V>MutableMap<K, MutableSet<V>>.safeAdd(key:K, value: V) {
	if(!this.containsKey(key))
		this[key] = mutableSetOf()
	this[key]!!.add(value)
}

fun <K,V>MutableMap<K, MutableSet<V>>.safeRemove(key: K, value: V) {
	if(this.containsKey(key))
		this[key]!!.remove(value)
}

interface NumberProperty<T: Number> : IProperty<T>

data class StringProperty(override val key: String, override var value: String) : IProperty<String>
data class BooleanProperty(override val key: String, override var value: Boolean): IProperty<Boolean>
data class IntegerProperty(override val key: String, override var value: Integer): NumberProperty<Integer>
