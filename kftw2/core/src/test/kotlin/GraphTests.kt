
import graph.Coordinate
import graph.Graph
import graph.TypedNode
import org.junit.BeforeClass
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class GraphTests {
	companion object {
		val dirs = mapOf(
				"north" to Pair(0,1),
				"northeast" to Pair(1,1),
				"east" to Pair(1,0),
				"southeast" to Pair(1,-1),
				"south" to Pair(0,-1),
				"southwest" to Pair(-1,-1),
				"west" to Pair(-1,0),
				"northwest" to Pair(-1,1))

		val dirs2 = mapOf(
				"north" 		to 	"south"  ,
				"northeast" to 	"southwest"  ,
				"east" 			to 	"west"  ,
				"southeast" to 	"northwest" ,
				"south" 		to 	"north" ,
				"southwest" to 	"northeast",
				"west" 			to 	"east" ,
				"northwest" to 	"southeast")

		@JvmStatic
		@BeforeClass
		fun beforeClass() {

		}
	}

	@Test
	fun graphAsMap() {
		for(side in 50..1000 step 100) {
			val graph = createGrid(side, side)

			println("Creating grid / map with $side x $side nodes (${side*side})")

			var nodes: Array<Array<TypedNode<Coordinate>>>? = null
			val nodeCreationTime = measureTimeMillis { nodes = getNodes(side, side) }

			println("Created all nodes in $nodeCreationTime")

			val fixNTime = measureTimeMillis { fixNeighbours(nodes!!) }

			println("Fixed neighbours in $fixNTime")

			println("time elapsed = ${measureTimeMillis { println(graph.nodes.map { it as TypedNode<Coordinate> }.prettyPrint(0, side - 1)) }}")
		}
	}

	private fun fixNeighbours(nodes: Array<Array<TypedNode<Coordinate>>>) {
		val maxX = nodes.lastIndex
		val maxY = maxX //Symmetric
		for((x, rows) in nodes.withIndex())
			for((y, node) in rows.withIndex()) {
				for((direction, offset) in dirs) {
					if(!node.hasRelation(direction)) {
						val tX = x + offset.first
						val tY = y + offset.second
						if(tX in 0..maxX && tY in 0..maxY) {
							val tNode = nodes[tX]!![tY]!!
							node.addRelation(direction, tNode)
							tNode.addRelation(dirs2[direction]!!, node)
						}
					}
				}
			}
	}

	fun startAtCenter(width: Int = 8, height: Int = 8) {

		/*
		Always start in a corner and imagine a small robot traversing the graph.
		 */
		val g = Graph(mapOf("width" to 100, "height" to height))
		val maxDistance = 10


		val node = TypedNode<Coordinate>(Coordinate(0,0))

		generate(g, node, maxDistance, 0)
	}

	fun generate(graph: Graph, node: TypedNode<Coordinate>, maxDistance:Int, distance: Int = 0) {
		if(distance < maxDistance) {
			for ((direction, offset) in dirs) {
				if (node.neighbour(direction) == null) {
					val n = TypedNode(Coordinate(node.data.x + offset.first, node.data.y + offset.second))
					node.addRelation(direction, n)
					n.addRelation(dirs2[direction]!!, node)
				}
			}
		}
	}

	fun getNodes(width: Int, height: Int) : Array<Array<TypedNode<Coordinate>>> {
		return Array<Array<TypedNode<Coordinate>>>(width) { x ->
			Array<TypedNode<Coordinate>>(height) { y ->
				TypedNode(Coordinate(x, y))
			}
		}
	}

	fun createGrid(width: Int = 8, height: Int = 8) : Graph {
		val g = Graph(mapOf("width" to width, "height" to height))

		val nodes = mutableMapOf<Coordinate, TypedNode<Coordinate>>()

		println("Created grid in: ${measureTimeMillis {



		for(x in 0 until width)
			for(y in 0 until height) {
				val node = TypedNode(Coordinate(x,y))
				nodes[node.data] = node
				g.addNode(node)
			}
		}
		}")


		println("Fixed neighbours in: ${measureTimeMillis {

			for ((coordinate, node) in nodes)
				for (xOff in -1..1)
					for (yOff in -1..1) {
						if (xOff != 0 || yOff != 0) {
							val x = coordinate.x + xOff
							val y = coordinate.y + yOff
							val tc = nodes.keys.firstOrNull { it.x == x && it.y == y }
							if (tc != null) {
								val relatedNode = nodes[tc]
									if(relatedNode != null) {
								node.addRelation(getRelationKey(xOff, yOff), relatedNode)
							}
							}
						}
					}

		}
		}")

		return g
	}

	fun getRelationKey(xOff: Int, yOff: Int): String {
		var direction = ""
		when(yOff) {
			-1 -> direction+="south"
			1 -> direction+="north"
		}

		when(xOff) {
			-1 -> direction+="west"
			1 -> direction+="east"
		}
		return direction
	}
}

fun Collection<TypedNode<Coordinate>>.prettyPrint(firstX:Int, firstY:Int) :String {
	//1. find top left coordinate
	/*
	We know the dimensions

	Another way is to traverse the node relations. So we start
	with the "top left" node, then just go east until we come to a node does
	not have a neighbour to the east, then just down from the first one!
	 */
	val sb = StringBuilder()
//	val minX = this.map { it.data.x }.min()!!
//	val maxY = this.map { it.data.y }.max()!!
	var topLeft: TypedNode<Coordinate>? = this.first { it.data.x == firstX && it.data.y == firstY}

	var currentNode : TypedNode<Coordinate>? = topLeft

	while (currentNode != null) {
		sb.append(currentNode.data.type)
		currentNode = currentNode.neighbour("east") as TypedNode<Coordinate>?
		if(currentNode == null) {
			currentNode = topLeft?.neighbour("south") as TypedNode<Coordinate>?
			topLeft = currentNode
			sb.appendln()
		}
	}

	return sb.toString()

	//2. What do we know of the height and width of the graph?
}

/*
For speed, we must work at some kind of depth or something...

So we start at 0,0

We traverse a collection of possible neighbours. For every direction
we create a relation. For every created related node, we must create a relation back
or it goes kaboom.
 */

fun addSomeNodes() {

}