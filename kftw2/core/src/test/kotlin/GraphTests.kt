
import com.badlogic.gdx.math.MathUtils
import graph.GraphEngine
import graph.INode
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphTests {
	companion object {

		val graphEngine = GraphEngine()

		@JvmStatic
		@BeforeClass
		fun beforeClass() {

		}
	}

	@Test
	fun addAndFindRelations() {
		/*
		What can we do, what can we do?

		All relations are directional, so adding a "has met" relation means
		we have to add it to both parties... or? Can we have different types?

		Relations will have properties, later.
		 */
		val playerNode = graphEngine.newNode()
		val npcNode = graphEngine.newNode()
		graphEngine.addBiDirectionalRelation(playerNode, npcNode, "has met")
		val otherNodes = mutableMapOf<INode, INode>()
		for(i in 0..10)
			otherNodes.put(graphEngine.newNode(), graphEngine.newNode())

		val relations = arrayOf("has seen", "hates", "killed", "loves", "wants to find")

		var index = 0

		for((first, second) in otherNodes) {
				graphEngine.addRelation(first, second, relations[index % 5])
				graphEngine.addRelation(second, first, relations[(index + 1) % 5])
			index++
		}

		val hasMet = graphEngine.relatedNodes(playerNode, "has met")
		assertTrue { hasMet.contains(npcNode) }
		assertFalse { hasMet.contains(playerNode) }
		assertEquals(1, hasMet.count())
	}

	@Test
	fun labelTest() {

		/*

		What "are" labels?

		In neo4j, labels are "types", so a node can have one or more labels
		for categorization or type management. So labels are just some text.

		So... how do we manage labels in the graphengine?

		Well, one might imagine that a node object has a list of its labels
		but also that the graphengine maintains a map of labels => nodes so that we can
		quickly find these nodes for a query. This might be an unnecessary pre-optimization
		so we can just start by having the nodes maintain their list.

		But how do we limit the addition-ability for the labels on node instances?

		This should be done with the internal modifier on the graph.Node, obviously
		 */

		val numberOfAdditionalNodes = 1000000
		val maxNumberOfRelationsPerNode = 100

		val otherNodes = mutableListOf<INode>()
		for(i in 0..numberOfAdditionalNodes) {
			val node = graphEngine.newNode("agent")
			otherNodes.add(node)
			if(i % 2 ==0) {
				graphEngine.addLabel(node, "vip")
			} else {
				graphEngine.addLabel(node, "npc")
			}
			if(i % 1000 == 0) {
				graphEngine.addLabel(node, "player")
			}
		}
		val relations = arrayOf("has met", "hates", "killed", "loves", "wants to find")

		var index = 0

		for((i, node) in otherNodes.withIndex()) {
			for(j in 0..MathUtils.random(5, maxNumberOfRelationsPerNode)) {
				var targetIndex = MathUtils.random(0, numberOfAdditionalNodes - 1)
				if(targetIndex == i)
					targetIndex++

				val targetNode = otherNodes[targetIndex]

				graphEngine.addRelation(node, targetNode, relations[index % relations.size])
				graphEngine.addRelation(targetNode, node, relations[(index + 1) % relations.size])
				index++
			}
		}

		println("numberOfNodes: $numberOfAdditionalNodes")

		//Test algo 2 and measure
		var time = measureTimeMillis {
			val nodeResult = graphEngine.find(listOf("player", "agent"), listOf("npc", "agent"), listOf("has met"), true)

			println("number of nodes when searching for nodes with ALL labels: ${nodeResult.count()}")
		}

		println("elapsed time: $time")
	}

	@Before
	fun before() {
	}

	@After
	fun after() {
	}
}
