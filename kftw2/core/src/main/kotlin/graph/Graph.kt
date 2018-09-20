package graph

class Graph(val graphProperties: Map<String, Any>) {
	val nodes = mutableSetOf<Node>()
	private val labels = mutableMapOf<String, MutableSet<Node>>()

	/*
	Different concept could be having the properties as a map of
	properties and then their values and THEN nodes that have those values...
	 */
	private val proppo = mutableMapOf<String, MutableMap<Property, MutableList<Node>>>()
	private val properties = mutableMapOf<String, Pair<Node, Property>>()

	fun addProperty(node: Node, property: Property) {
		if(!proppo.containsKey(property.name))
			proppo[property.name] = mutableMapOf()

		if(!proppo[property.name]!!.containsKey(property))
			proppo[property.name]!![property] = mutableListOf()

		proppo[property.name]!![property]!!.add(node)
	}



	fun addLabel(label:String, node:Node) {
		if(!labels.containsKey(label))
			labels[label] = mutableSetOf()

		labels[label]!!.add(node)
	}

	fun removeLabel(label:String, node:Node) {
		if(!labels.containsKey(label)) return

		labels[label]!!.remove(node)
	}

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


open class Node {
	private val relations = mutableMapOf<String, MutableSet<Node>>()

	fun addRelation(name:String, relatedNode: Node) {
		if(!relations.containsKey(name))
			relations[name] = mutableSetOf()

		relations[name]!!.add(relatedNode)
	}

	fun neigbours(relationToFind:String) : Sequence<Node> {
		return if(relations.containsKey(relationToFind)) relations[relationToFind]!!.asSequence() else emptySequence()
	}

	fun neighbours(relationsToFind: Collection<String>) : Sequence<Node> {
		return relations.filterKeys { relationsToFind.contains(it) }.flatMap { it.value }.asSequence()
	}
}

data class TypedNode<T>(val data: T) : Node()

abstract class Property {
	abstract val name: String
}

abstract class TypedProperty<T> : Property() {
	abstract val value: T
}

data class GenericTypedProperty<T>(override val name: String, override val value:T):TypedProperty<T>()
data class StringProperty(override val name: String, override val value: String) : TypedProperty<String>()
data class IntProperty(override val name: String, override val value: Int) : TypedProperty<Int>()
data class BoolProperty(override val name: String, override val value: Boolean) : TypedProperty<Boolean>()
data class Coordinate(val x: Int, val y: Int, var type: Int = 0)
