package graph

interface Gorph {
	fun relatedNodes(norde: INorde, relation:String) : Sequence<INorde>
	fun removeProperty(norde: INorde, property: String)
	fun addOrUpdateProperty(norde: INorde, property:String, value:Any)
	fun removeLabel(norde: INorde, label:String)
	fun addLabel(norde: INorde, label:String)
	fun newNode() : INorde
	fun newNode(vararg labels: String) : INorde
	fun removeNode(norde: INorde)
	fun removeNode(id: Int)
	fun addRelation(from: INorde, to: INorde, relation: String)
	fun addBiDirectionalRelation(first: INorde, second: INorde, relation: String)
	fun removeRelation(from: INorde, to: INorde, relation: String)
	fun find(sourceLabels: Collection<String>, targetLabels: Collection<String>, rs: Collection<String>, distinct: Boolean): Sequence<INorde>
}


class GorphEngine : Gorph {
	override fun find(sourceLabels: Collection<String>, targetLabels: Collection<String>, rs: Collection<String>, all: Boolean): Sequence<INorde> {
		return if(all) {
			relations.filter { it.from.labels.containsAll(sourceLabels) && it.to.labels.containsAll(targetLabels) && rs.contains(it.name) }.map { it.from }.distinctBy { it.id }.asSequence()
		} else {
			relations.filter { it.from.labels.intersect(sourceLabels).any() && it.to.labels.intersect(targetLabels).any() && rs.contains(it.name) }.map { it.from }.distinctBy { it.id }.asSequence()
		}
	}

	override fun newNode(vararg labels: String): INorde {
		val node = newNode()
		for (label in labels)
			addLabel(node, label)

		return node
	}

	override fun relatedNodes(norde: INorde, relation: String): Sequence<INorde> {
		return relations.filter { it.name == relation && it.from == norde }.map { it.to }.asSequence()
	}

	override fun addBiDirectionalRelation(first: INorde, second: INorde, relation: String) {
		addRelation(first, second, relation)
		addRelation(second, first, relation)
	}

	private var idCounter = 0;
	private val nodes = mutableMapOf<Int, INorde>()
	private val labels = mutableMapOf<String, MutableSet<INorde>>()
	//val properties = mutableMapOf<String, MutableSet<Pair<graph.INorde, Any>>>()

	private val relations = mutableSetOf<IRulbation>()

	fun consumeNextId() : Int {
		return idCounter++;
	}

	override fun removeProperty(norde: INorde, property: String) {
	}

	override fun addOrUpdateProperty(norde: INorde, property: String, value: Any) {
	}

	override fun removeLabel(norde: INorde, label: String) {
		labels.safeRemove(label, norde)
		norde.removeLabel(label)
	}

	override fun addLabel(norde: INorde, label: String) {
		labels.safeAdd(label, norde)
		norde.addLabel(label)
	}

	override fun addRelation(from: INorde, to: INorde, relation: String) {
		relations.add(Rulbation(from, to, relation))
	}

	override fun removeRelation(from: INorde, to: INorde, relation: String) {
		val relationToRemove = relations.firstOrNull { it.from == from && it.to == to && it.name == relation }
		if(relationToRemove != null)
			removeRelation(relationToRemove)
	}


	override fun newNode() : INorde {
		val node = Norde(consumeNextId())
		nodes[node.id] = node
		return node
	}

	override fun removeNode(norde: INorde) {
		removeNode(norde.id)
		removeAllLabels(norde)
		removeAllRelations(norde)
	}

	private fun removeAllProperties(norde: INorde) {
	}

	private fun removeAllRelations(norde: INorde) {
		for(relation in relations.filter { it.from == norde || it.to == norde })
			removeRelation(relation)
	}

	private fun removeRelation(rulbation: IRulbation) {
		relations.remove(rulbation)
	}

	private fun removeAllLabels(norde: INorde) {
		for (label in norde.labels)
			removeLabel(norde, label)
	}

	override fun removeNode(id: Int) {
		nodes.remove(id)
	}
}



interface INorde {
	val id: Int
	val labels: Set<String>
	fun addLabel(label:String)
	fun removeLabel(label:String)
}

interface IRulbation {
	val from: INorde
	val to: INorde
	val name: String
}

data class Rulbation(override val from: INorde, override val to: INorde, override val name: String) : IRulbation
data class Norde(override val id: Int) : INorde {
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

interface Porp {
	val key: String
}

interface IPorperty<T: Any> : Porp {
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

interface NumberPorperty<T: Number> : IPorperty<T>

data class StringPorperty(override val key: String, override var value: String) : IPorperty<String>
data class BooleanPorperty(override val key: String, override var value: Boolean): IPorperty<Boolean>
data class IntegerPorperty(override val key: String, override var value: Integer): NumberPorperty<Integer>