
import graph.Coordinate
import graph.Graph
import graph.Node
import graph.TypedNode
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphTests {
	companion object {
		@JvmStatic
		@BeforeClass
		fun beforeClass() {

		}
	}

	@Test
	fun graphAsMap() {
		val graph = createGrid(20, 20)

		assertEquals(400, graph.nodes.count())
		println(graph.nodes.map { it as TypedNode<Coordinate> }.prettyPrint())
	}

	fun createGrid(width: Int = 8, height: Int = 8) : Graph {
		val g = Graph(mapOf("width" to width, "height" to height))

		val nodes = mutableListOf<TypedNode<Coordinate>>()

		for(x in 0 until width)
			for(y in 0 until height) {
				val node = TypedNode(Coordinate(x,y))
				nodes.add(node)
				g.addNode(node)
			}

		for(node in nodes) {
			for(xOff in -1..1)
				for (yOff in -1..1)
				{
					val x = node.data.x + xOff
					val y = node.data.y + yOff
					val relatedNode = nodes.firstOrNull { it.data.x == x && it.data.y == y}
					if(relatedNode != null && relatedNode != node) {
						node.addRelation(getRelationKey(xOff, yOff), relatedNode)
					}
				}
		}

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

fun Collection<TypedNode<Coordinate>>.prettyPrint() :String {
	//1. find top left coordinate
	/*
	We know the dimensions

	Another way is to traverse the node relations. So we start
	with the "top left" node, then just go east until we come to a node does
	not have a neighbour to the east, then just down from the first one!
	 */
	val sb = StringBuilder()
	val minX = this.map { it.data.x }.min()!!
	val maxY = this.map { it.data.y }.max()!!
	var topLeft: TypedNode<Coordinate>? = this.first { it.data.x == minX && it.data.y == maxY }

	var currentNode : TypedNode<Coordinate>? = topLeft

	while (currentNode != null) {
		sb.append(currentNode.data.type)
		currentNode = currentNode.neigbours("east").firstOrNull() as TypedNode<Coordinate>?
		if(currentNode == null) {
			currentNode = topLeft?.neigbours("south")?.firstOrNull() as TypedNode<Coordinate>?
			topLeft = currentNode
			sb.appendln()
		}
	}

	return sb.toString()

	//2. What do we know of the height and width of the graph?
}