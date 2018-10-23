package graph

class Graph<T>(val graphProperties: Map<String, Any>) {
	val nodes = mutableSetOf<Node<T>>()
	private val labels = mutableMapOf<String, MutableSet<Node<T>>>()
	//PropertyMap just contains all properties that actually HAVE a property, not their values.
	val propertyMap = mutableMapOf<String, MutableSet<Node<T>>>()

	fun addProperty(node: Node<T>, property: Property<Any>) {
		if(!propertyMap.containsKey(property.name))
			propertyMap[property.name] = mutableSetOf()

		propertyMap[property.name]!!.add(node)
		node.addProperty(property)
	}

	fun removeProperty(node: Node<T>, property: Property<Any>) {
		if(propertyMap.containsKey(property.name))
			propertyMap[property.name]!!.remove(node)
		node.removeProperty(property)
	}

	fun addLabel(label:String, node:Node<T>) {
		if(!labels.containsKey(label))
			labels[label] = mutableSetOf()

		labels[label]!!.add(node)
	}

	fun removeLabel(label:String, node:Node<T>) {
		if(!labels.containsKey(label)) return

		labels[label]!!.remove(node)
	}

	fun addNode(node: Node<T>) {
		nodes.add(node)
	}

	fun addNodes(vararg node:Node<T>) {
		nodes.addAll(node)
	}

	fun removeNodes(vararg node:Node<T>) {

		//Shit, this is more complex...
	}

	fun thatHaveProperties(nodes: Collection<Node<T>>, vararg propertiesToFind:String): Sequence<Node<T>> {
		return propertyMap.filterKeys { propertiesToFind.contains(it) }.flatMap { it.value }.asSequence()
	}

	fun withLabels(nodes: Collection<Node<T>>, vararg labelsToFind: String):Sequence<Node<T>> {
		return labels.filterKeys { labelsToFind.contains(it) }.flatMap { it.value }.intersect(nodes).asSequence()
	}
}


data class Node<T>(val data: T) {
	private val relations = mutableMapOf<String, MutableSet<Node<T>>>()
	val allNeighbours: Iterable<Node<T>> get() = relations.map { it.value }.flatten()

	fun addRelation(name:String, relatedNode: Node<T>) {
		if(!relations.containsKey(name))
			relations[name] = mutableSetOf()

		relations[name]!!.add(relatedNode)
	}

	fun neighbours(relationToFind:String) : Sequence<Node<T>> {
		return if(relations.containsKey(relationToFind)) relations[relationToFind]!!.asSequence() else emptySequence()
	}

	fun neighbour(relationToFind: String) : Node<T>? {
		return relations[relationToFind]?.firstOrNull()
	}

	fun neighbours(relationsToFind: Collection<String>) : Sequence<Node<T>> {
		return relations.filterKeys { relationsToFind.contains(it) }.flatMap { it.value }.asSequence()
	}

	fun hasRelation(relation: String): Boolean {
		return relations.containsKey(relation)
	}

	private val properties = mutableMapOf<String, Property<Any>>()

	fun addProperty(property: Property<Any>) {
		properties[property.name] = property
	}

	fun removeProperty(property: Property<Any>) {
		properties.remove(property.name)
	}
}

abstract class Property<T> {
	abstract val name: String
	abstract var value: T
}

data class GenericTypedProperty<T>(override val name: String, override var value:T):Property<T>()
data class StringProperty(override val name: String, override var value: String) : Property<String>()
data class IntProperty(override val name: String, override var value: Int) : Property<Int>()
data class BoolProperty(override val name: String, override var value: Boolean) : Property<Boolean>()
data class Coordinate(val x: Int, val y: Int, var type: Int = 0)