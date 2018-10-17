package graph

class Graph<T, R>(val graphProperties: Map<String, Any>) {
	val nodes = mutableSetOf<Node<T, R>>()
	private val labels = mutableMapOf<String, MutableSet<Node<T, R>>>()
	//PropertyMap just contains all properties that actually HAVE a property, not their values.
	val propertyMap = mutableMapOf<String, MutableSet<Node<T, R>>>()

	fun addProperty(node: Node<T, R>, property: Property<Any>) {
		if(!propertyMap.containsKey(property.name))
			propertyMap[property.name] = mutableSetOf()

		propertyMap[property.name]!!.add(node)
		node.addProperty(property)
	}

	fun removeProperty(node: Node<T, R>, property: Property<Any>) {
		if(propertyMap.containsKey(property.name))
			propertyMap[property.name]!!.remove(node)
		node.removeProperty(property)
	}

	fun addLabel(label:String, node:Node<T, R>) {
		if(!labels.containsKey(label))
			labels[label] = mutableSetOf()

		labels[label]!!.add(node)
	}

	fun removeLabel(label:String, node:Node<T, R>) {
		if(!labels.containsKey(label)) return

		labels[label]!!.remove(node)
	}

	fun addNode(node: Node<T, R>) {
		nodes.add(node)
	}

	fun addNodes(nodesToAdd:Collection<Node<T, R>>) {
		nodes.addAll(nodesToAdd)
	}

	fun removeNodes(vararg node:Node<T, R>) {

		//Shit, this is more complex...
	}

	fun thatHaveProperties(nodes: Collection<Node<T, R>>, vararg propertiesToFind:String): Sequence<Node<T, R>> {
		return propertyMap.filterKeys { propertiesToFind.contains(it) }.flatMap { it.value }.asSequence()
	}

	fun withLabels(nodes: Collection<Node<T, R>>, vararg labelsToFind: String):Sequence<Node<T, R>> {
		return labels.filterKeys { labelsToFind.contains(it) }.flatMap { it.value }.intersect(nodes).asSequence()
	}
}


data class Node<T, R>(val data: T) {
	private val relations = mutableMapOf<R, MutableSet<Node<T, R>>>()
	val allNeighbours: Iterable<Node<T, R>> get() = relations.map { it.value }.flatten()

	fun addRelation(relation:R, relatedNode: Node<T, R>) {
		if(!relations.containsKey(relation))
			relations[relation] = mutableSetOf()

		relations[relation]!!.add(relatedNode)
	}

	fun neighbours(relationToFind:R) : Sequence<Node<T, R>> {
		return if(relations.containsKey(relationToFind)) relations[relationToFind]!!.asSequence() else emptySequence()
	}

	fun neighbour(relationToFind: R) : Node<T, R>? {
		return relations[relationToFind]?.firstOrNull()
	}

	fun neighbours(relationsToFind: Collection<R>) : Sequence<Node<T, R>> {
		return relations.filterKeys { relationsToFind.contains(it) }.flatMap { it.value }.asSequence()
	}

	fun hasRelation(relation: R): Boolean {
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
