package graph

interface Graph {
	fun relatedNodes(node: INode, relation:String) : Sequence<INode>
	fun removeProperty(node: INode, property: String)
	fun addOrUpdateProperty(node: INode, property:String, value:Any)
	fun removeLabel(node: INode, label:String)
	fun addLabel(node: INode, label:String)
	fun newNode() : INode
	fun newNode(vararg labels: String) : INode
	fun removeNode(node: INode)
	fun removeNode(id: Int)
	fun addRelation(from: INode, to: INode, relation: String)
	fun addBiDirectionalRelation(first: INode, second: INode, relation: String)
	fun removeRelation(from: INode, to: INode, relation: String)
	fun find(sourceLabels: Collection<String>, targetLabels: Collection<String>, rs: Collection<String>, distinct: Boolean): Sequence<INode>
}


class GraphEngine : Graph {
	override fun find(sourceLabels: Collection<String>, targetLabels: Collection<String>, rs: Collection<String>, all: Boolean): Sequence<INode> {
		return if(all) {
			relations.filter { it.from.labels.containsAll(sourceLabels) && it.to.labels.containsAll(targetLabels) && rs.contains(it.name) }.map { it.from }.distinctBy { it.id }.asSequence()
		} else {
			relations.filter { it.from.labels.intersect(sourceLabels).any() && it.to.labels.intersect(targetLabels).any() && rs.contains(it.name) }.map { it.from }.distinctBy { it.id }.asSequence()
		}
	}

	override fun newNode(vararg labels: String): INode {
		val node = newNode()
		for (label in labels)
			addLabel(node, label)

		return node
	}

	override fun relatedNodes(node: INode, relation: String): Sequence<INode> {
		return relations.filter { it.name == relation && it.from == node }.map { it.to }.asSequence()
	}

	override fun addBiDirectionalRelation(first: INode, second: INode, relation: String) {
		addRelation(first, second, relation)
		addRelation(second, first, relation)
	}

	private var idCounter = 0;
	private val nodes = mutableMapOf<Int, INode>()
	private val labels = mutableMapOf<String, MutableSet<INode>>()
	//val properties = mutableMapOf<String, MutableSet<Pair<graph.INode, Any>>>()

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
data class Node(override val id: Int) : INode {
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