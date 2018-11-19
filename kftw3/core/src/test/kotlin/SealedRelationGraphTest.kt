import com.badlogic.gdx.math.MathUtils
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graph.Coordinate
import graph.Graph
import graph.Node
import org.junit.BeforeClass
import world.*
import kotlin.test.Test

/*
I utforskande programmering arbetar vi med
"hypoteser". Vi har en hypotes kring hur vi kan utföra eller lösa en sak.
Den hypotesen testar vi sen. Att ett spel inte blir färdigt eller
ens nära färdigt IDAG spelar ingen roll, allt handlar om utforskande
programmering och närmast "forskning" kring hypoteser.

Nu ska vi testa hypoteser kring karta och karta som pussel
med en graf som datastruktur, där relationerna är sealed classes.
Fine. Det behöver inte funka - men det är bra om det INTE funkar
tidigt.

Vilket är anledningen till att vi kanske gör det som enhetstester!

Directions as fucking enums, jag är ett geni!
 */


class SealedRelationGraphTest {
  companion object {


    @JvmStatic
    @BeforeClass
    fun beforeClass() {

    }
  }

  @Test
  fun worldGraph() {
    val nodes = mutableMapOf<Coordinate, Node<Coordinate, MapRelation>>()
    val xMin = 0
    val xMax = 99
    val yMin = 0
    val yMax = 99
    val graph = Graph<Coordinate, MapRelation>(mapOf())

    /*
    We shall traverse the bounds of the x-y-max and for every node create or find neighbours etc.
     */
    for(x in xMin..xMax)
      for(y in yMin..yMax) {
        /*
        All hail satan!

        We know what relations we ACTUALLY need etc... make another map-map of
        compassdirections on the form "eastern" -> "northeast", "east", "southeast". Fucking-ah
         */

        val node = nodes.getNode(x,y)
        /*
        What relations do we need?
         */
        val anchor = getAnchor(x, y, xMin, xMax, yMin, yMax)

        val neededRelations = MapStuff.getCompassDirectionsFor(anchor).map { MapStuff.neighbourRelation[it]!! }
        for (relation in neededRelations) {
          if(!node.hasRelation(relation)) {
            val direction = MapStuff.dirs[relation.toThe]!!
            val relatedNode = nodes.getNode(x + direction.first, y + direction.second)
            node.addRelation(relation, relatedNode)

            relatedNode.addRelation(MapStuff.neighbourRelation[MapStuff.opposites[relation.toThe]!!]!!, node)
          }
        }
      }

    /*
    Hail Satan!

    When that is done, we shall be done - the code written, the code done

    We should have a list of 9, no more, no less, nodes.
     */
    graph.addNodes(nodes.values)
  }

  @Test
  fun createPortals() {
    val worldGraph = MapBuilder.createWorld(0,0,3,3)
    /*
    Hail the Dark Lord!

    Here we have a graph with nodes that are connected back and forth like... a lot.

    So we need to... traverse the graph and for every node create new relations to other nodes.

    Every node should have... four puzzle relations. This is different than the actual neighbour
    relations, since we will now only have four portals.

    So every node must have basically four portals going out and four going in. Our start
    hypothesis is that we won't need to have the same in-out-relationship. Now, a node that has less
    than four of the cardinal directions going in must only have that many portal relations.

    This could get reaaaal complicated! ;-)

    Since it won't matter at all which nodes we go TO, we don't need a map for the finding of target nodes.

    But how do we keep track of in and out nodes?

    We could have a map of relations. Or two maps, ingoing and outgoing.

    No, we build several maps of AVAILABLE nodes for a particular direction. So, for every node, add
    it to a map if it CAN have a particular portaldirection. Then when we traverse ALL nodes and
    have to choose a random node in some direction, we just remove it from that map - that way it cannot
    be used twice. Fucking gold, man!

    Or no, it isn't gold. Well yes, it is fucking gold. Changed my mind.

     */

    val outMap = mutableMapOf<CompassDirection, MutableSet<Node<Coordinate, MapRelation>>>()
    outMap[CompassDirection.NORTH] = mutableSetOf()
    outMap[CompassDirection.EAST] = mutableSetOf()
    outMap[CompassDirection.SOUTH] = mutableSetOf()
    outMap[CompassDirection.WEST] = mutableSetOf()

    val inMap = mutableMapOf<CompassDirection, MutableSet<Node<Coordinate, MapRelation>>>()
    inMap[CompassDirection.NORTH] = mutableSetOf()
    inMap[CompassDirection.EAST] = mutableSetOf()
    inMap[CompassDirection.SOUTH] = mutableSetOf()
    inMap[CompassDirection.WEST] = mutableSetOf()

    for(node in worldGraph.nodes) {
      for((direction, outNodes) in outMap)
      if(node.hasRelation(MapStuff.neighbourRelation[direction]!!)) {
        outNodes.add(node)
        inMap[direction]!!.add(node)
      }
    }

    for ((direction, outNodes) in outMap) {
      //We don't need to remove the outNodes, we loop over all of them!
      for (outNode in outNodes) {
        //Select a direction at random!
        val targetDirections = inMap.filter { it.value.any() }.map { it.key }
        val targetDirection = targetDirections.elementAt(MathUtils.random(0, targetDirections.count() - 1))

        val possibleTargets = inMap[targetDirection]!!.filter { it != outNode }

        var targetIndex = MathUtils.random(0, possibleTargets.count() - 1)
        targetIndex = if(targetIndex < 0) 0 else targetIndex
        val targetNode = possibleTargets.elementAt(targetIndex)

        outNode.addRelation(MapStuff.portalRelation[direction]!!, targetNode)
        inMap[targetDirection]!!.remove(targetNode)
      }
    }
  }

  @Test
  fun serializationTest() {
    //1. First, create graph
    val worldGraph = MapBuilder.createWorld(0,0,3,3)
    //2. Serialize to JSON
    val mapper = jacksonObjectMapper()
    val writer = mapper.writerWithDefaultPrettyPrinter()
    val data = writer.writeValueAsString(worldGraph)

    //3. read?

    val readWorld = mapper.readValue(data, Graph::class.java)
  }
}

inline fun <R> measureAndReturn(block: () -> R): Pair<R, Long> {
  val start = System.currentTimeMillis()
  val result = block()
  return result to (System.currentTimeMillis() - start)
}

