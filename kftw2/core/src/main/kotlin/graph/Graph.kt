package graph

class Graph {
	private val nodes = mutableSetOf<Node>()
	private val labels = mutableMapOf<String, MutableSet<Node>>()
	private val properties = mutableMapOf<String, Pair<Node, PropertyValue>>()

	fun addNode(node: Node) {
		nodes.add(node)
	}

	fun thatHaveProperties(nodes: Collection<Node>, vararg propertiesToFind:String): Sequence<Node> {
		return properties.filterKeys { propertiesToFind.contains(it) }.map { it.value }.map { it.first }.intersect(nodes).asSequence()
	}

	fun withLabels(nodes: Collection<Node>, vararg labelsToFind: String):Sequence<Node> {
		return labels.filterKeys { labelsToFind.contains(it) }.flatMap { it.value }.intersect(nodes).asSequence()
	}
}


data class Node(val name: String) {
	private val relations = mutableMapOf<String, MutableSet<Node>>()

	fun neigbours(relationToFind:String) : Sequence<Node> {
		return if(relations.containsKey(relationToFind)) relations[relationToFind]!!.asSequence() else emptySequence()
	}

	fun neighbours(relationsToFind: Collection<String>) : Sequence<Node> {
		return relations.filterKeys { relationsToFind.contains(it) }.flatMap { it.value }.asSequence()
	}
}

abstract class PropertyValue

abstract class TypedPropertyValue<T> {
	abstract var value: T
}

data class StringValue(override var value: String) : TypedPropertyValue<String>()
data class IntValue(override var value: Int) : TypedPropertyValue<Int>()
data class BoolValue(override var value: Boolean) : TypedPropertyValue<Boolean>()
