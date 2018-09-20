
import graph.Coordinate
import graph.Graph
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
	fun addAndFindRelations() {
		val graph = createGrid()

		assertEquals(64, graph.nodes.count())

		val test = ""

	}

	fun createGrid(width: Int = 8, height: Int = 8) : Graph {
		val g = Graph()

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