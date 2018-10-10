import graph.Coordinate
import graph.Node
import org.junit.BeforeClass
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

enum class CompassDirection {
  NORTH,
  NORTHEAST,
  EAST,
  SOUTHEAST,
  SOUTH,
  SOUTHWEST,
  WEST,
  NORTHWEST
}

sealed class MapRelations {
  data class Neighbour(val toThe: CompassDirection)
  data class Portal(val toThe: CompassDirection)
}

class SealedRelationGraphTest {
  companion object {


    @JvmStatic
    @BeforeClass
    fun beforeClass() {

    }
  }

  @Test
  fun worldMap_Creation() {


  }
}

object MapStuff {
  val dirs: Map<CompassDirection, Pair<Int, Int>>
    get() = mapOf(
        CompassDirection.NORTH      to Pair(0,1),
        CompassDirection.NORTHEAST  to Pair(1,1),
        CompassDirection.EAST       to Pair(1,0),
        CompassDirection.SOUTHEAST  to Pair(1,-1),
        CompassDirection.SOUTH      to Pair(0,-1),
        CompassDirection.SOUTHWEST  to Pair(-1,-1),
        CompassDirection.WEST       to Pair(-1,0),
        CompassDirection.NORTHWEST  to Pair(-1,1))

  val opposites: Map<CompassDirection, CompassDirection>
    get() = mapOf(
        CompassDirection.NORTH      	to 	CompassDirection.SOUTH,
        CompassDirection.NORTHEAST   to 	CompassDirection.SOUTHWEST,
        CompassDirection.EAST       	to 	CompassDirection.WEST,
        CompassDirection.SOUTHEAST   to 	CompassDirection.NORTHWEST,
        CompassDirection.SOUTH      	to 	CompassDirection.NORTH,
        CompassDirection.SOUTHWEST   to 	CompassDirection.NORTHEAST,
        CompassDirection.WEST       	to 	CompassDirection.EAST,
        CompassDirection.NORTHWEST   to 	CompassDirection.SOUTHEAST)
}

object MapBuilder {



  fun getNodes(width: Int, height: Int) : Array<Array<Node<Coordinate, String>>> {
    return Array<Array<Node<Coordinate, String>>>(width) { x ->
      Array<Node<Coordinate, String>>(height) { y ->
        Node(Coordinate(x, y))
      }
    }
  }

  fun fixNeighbours(nodes: Array<Array<Node<Coordinate, MapRelations>>>) {
    val maxX = nodes.lastIndex
    val maxY = maxX //Symmetric
    for((x, rows) in nodes.withIndex())
      for((y, node) in rows.withIndex()) {
        for((direction, offset) in MapStuff.dirs) {
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
}