
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

		val hasMet = graphEngine.relatedNodes(playerNode, "has met")
		assertTrue { hasMet.contains(npcNode) }
		assertFalse { hasMet.contains(playerNode) }
		assertEquals(1, hasMet.count())
	}

	@Test
	fun labelTest() {

		/*

		What "are" labels?

		In neo4j, labels are "types", so a node can have one or more labels
		for categorization or type management. So labels are just some text.

		So... how do we manage labels in the graphengine?

		Well, one might imagine that a node object has a list of its labels
		but also that the graphengine maintains a map of labels => nodes so that we can
		quickly find these nodes for a query. This might be an unnecessary pre-optimization
		so we can just start by having the nodes maintain their list.

		But how do we limit the addition-ability for the labels on node instances?

		This should be done with the internal modifier on the Node, obviously
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

		//Add some labels and use for a query?


	}

	@Before
	fun before() {
	}

	@After
	fun after() {
	}
}

interface Graph {
	fun relatedNodes(node:INode, relation:String) : Sequence<INode>
	fun removeProperty(node: INode, property: String)
	fun addOrUpdateProperty(node:INode, property:String, value:Any)
	fun removeLabel(node: INode, label:String)
	fun addLabel(node:INode, label:String)
	fun newNode() : INode
	fun newNode(vararg labels: String) : INode
	fun removeNode(node:INode)
	fun removeNode(id: Int)
	fun addRelation(from:INode, to:INode, relation: String)
	fun addBiDirectionalRelation(first:INode, second:INode, relation: String)
	fun removeRelation(from:INode, to: INode, relation: String)
}

class GraphEngine : Graph {
	override fun newNode(vararg labels: String): INode {
		val node = newNode()
		for (label in labels)
			addLabel(node, label)
	}

	override fun relatedNodes(node: INode, relation: String): Sequence<INode> {
		return relations.filter { it.name == relation && it.from == node }.map { it.to }.asSequence()
	}

	override fun addBiDirectionalRelation(first: INode, second: INode, relation: String) {
		addRelation(first, second, relation)
		addRelation(second, first, relation)
	}

	private var idCounter = 0;
	private val nodes = mutableMapOf<Int,INode>()
	private val labels = mutableMapOf<String, MutableSet<INode>>()
	//val properties = mutableMapOf<String, MutableSet<Pair<INode, Any>>>()

	private val relations = mutableSetOf<IRelation>()

	fun consumeNextId() : Int {
		return idCounter++;
	}

	override fun removeProperty(node: INode, property: String) {
	}

	override fun addOrUpdateProperty(node: INode, property: String, value: Any) {
	}

	override fun removeLabel(node: INode, label: String) {
		labels.safeRemove(label, node)
		node.removeLabel(label)
	}

	override fun addLabel(node: INode, label: String) {
		labels.safeAdd(label, node)
		node.addLabel(label)
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
		for (label in node.labels)
			removeLabel(node, label)
	}

	override fun removeNode(id: Int) {
		nodes.remove(id)
	}
}



interface INode {
	val id: Int
	val labels: Set<String>
	fun addLabel(label:String)
	fun removeLabel(label:String)
}

interface IRelation {
	val from: INode
	val to: INode
	val name: String
}

data class Relation(override val from: INode, override val to: INode, override val name: String) : IRelation
data class Node(override val id: Int) :INode {
	val internalLabels = mutableSetOf<String>()
	override val labels: Set<String>
		get() = internalLabels

	override fun addLabel(label: String) {
		internalLabels.add(label)
	}

	override fun removeLabel(label: String) {
		internalLabels.remove(label)
	}
}

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
