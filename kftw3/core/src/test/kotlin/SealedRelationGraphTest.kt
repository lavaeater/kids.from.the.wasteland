import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graph.Coordinate
import graph.Graph
import graph.Node
import org.junit.BeforeClass
import world.*
import kotlin.system.measureTimeMillis
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
    val nodes = mutableMapOf<Coordinate, Node<Coordinate, MapRelations>>()
    val xMin = 0
    val xMax = 99
    val yMin = 0
    val yMax = 99
    val graph = Graph<Coordinate, MapRelations>(mapOf())

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

        val neededRelations = MapStuff.getCompassDirectionsFor(anchor).map { MapStuff.neighbourRelations[it]!! }
        for (relation in neededRelations) {
          if(!node.hasRelation(relation)) {
            val direction = MapStuff.dirs[relation.toThe]!!
            val relatedNode = nodes.getNode(x + direction.first, y + direction.second)
            node.addRelation(relation, relatedNode)

            relatedNode.addRelation(MapStuff.neighbourRelations[MapStuff.opposites[relation.toThe]!!]!!, node)
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
  fun serializationTest() {
    //1. First, create graph
    val worldGraph = MapBuilder.createWorld(0,0,10,10)
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

