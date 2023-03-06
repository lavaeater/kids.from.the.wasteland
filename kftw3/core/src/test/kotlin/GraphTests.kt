
import graph.Coordinate
import graph.Node
import org.junit.BeforeClass
import org.junit.Test
import kotlin.system.measureTimeMillis

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
	fun graphPerformance() {
		val side = 1000
		println("Creating grid / map with $side x $side nodes (${side * side})")

		var nodes: Array<Array<Node<Coordinate, String>>>? = null
		val nodeCreationTime = measureTimeMillis { nodes = getNodes(side, side) }

		println("Created all nodes in $nodeCreationTime")

		val fixNTime = measureTimeMillis { fixNeighbours(nodes!!) }

		println("Fixed neighbours in $fixNTime")

		println("time elapsed = ${measureTimeMillis { println(nodes!!.flatten().prettyPrint(0, side - 1)) }}")

	}

	private fun fixNeighbours(nodes: Array<Array<Node<Coordinate, String>>>) {
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

	fun getNodes(width: Int, height: Int) : Array<Array<Node<Coordinate, String>>> {
		return Array<Array<Node<Coordinate, String>>>(width) { x ->
			Array<Node<Coordinate, String>>(height) { y ->
				Node(Coordinate(x, y))
			}
		}
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

fun Collection<Node<Coordinate, String>>.prettyPrint(firstX:Int, firstY:Int) :String {
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
	var topLeft: Node<Coordinate, String>? = this.first { it.data.x == firstX && it.data.y == firstY}

	var currentNode : Node<Coordinate, String>? = topLeft

	while (currentNode != null) {
		sb.append(currentNode.data.type)
		currentNode = currentNode.neighbour("east")
		if(currentNode == null) {
			currentNode = topLeft?.neighbour("south")
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