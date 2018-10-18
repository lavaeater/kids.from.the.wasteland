package world

import com.badlogic.gdx.math.MathUtils
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import graph.Coordinate
import graph.Graph
import graph.Node
import java.util.UUID

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

enum class CardinalDirection {
  NORTH,
  EAST,
  SOUTH,
  WEST
}

enum class Anchor {
  TOP,
  TOPRIGHT,
  RIGHT,
  BOTTOMRIGHT,
  BOTTOM,
  BOTTOMLEFT,
  LEFT,
  TOPLEFT,
  CENTER
}

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
sealed class MapRelation {
  var id = UUID.randomUUID()
  data class Neighbour(val toThe: CompassDirection) : MapRelation()
  data class Portal(val toThe: CompassDirection) : MapRelation()
}


object MapStuff {
  val coordinateCache = mutableSetOf<Coordinate>()

  val neighbourRelation: Map<CompassDirection, MapRelation.Neighbour>
    get() = mapOf(
        CompassDirection.NORTH      to MapRelation.Neighbour(CompassDirection.NORTH),
        CompassDirection.NORTHEAST  to MapRelation.Neighbour(CompassDirection.NORTHEAST),
        CompassDirection.EAST       to MapRelation.Neighbour(CompassDirection.EAST),
        CompassDirection.SOUTHEAST  to MapRelation.Neighbour(CompassDirection.SOUTHEAST),
        CompassDirection.SOUTH      to MapRelation.Neighbour(CompassDirection.SOUTH),
        CompassDirection.SOUTHWEST  to MapRelation.Neighbour(CompassDirection.SOUTHWEST),
        CompassDirection.WEST       to MapRelation.Neighbour(CompassDirection.WEST),
        CompassDirection.NORTHWEST  to MapRelation.Neighbour(CompassDirection.NORTHWEST)
    )

  val portalRelation: Map<CompassDirection, MapRelation.Portal>
    get() = mapOf(
        CompassDirection.NORTH      to MapRelation.Portal(CompassDirection.NORTH),
        CompassDirection.EAST       to MapRelation.Portal(CompassDirection.EAST),
        CompassDirection.SOUTH      to MapRelation.Portal(CompassDirection.SOUTH),
        CompassDirection.WEST       to MapRelation.Portal(CompassDirection.WEST)
    )
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

  fun getCompassDirectionsFor(anchor: Anchor): Set<CompassDirection> {
    when(anchor) {
      Anchor.CENTER -> return CompassDirection.values().toSet()
      Anchor.BOTTOMLEFT -> return setOf(
          CompassDirection.NORTH,
          CompassDirection.NORTHEAST,
          CompassDirection.EAST)
      Anchor.LEFT -> return setOf(
          CompassDirection.NORTH,
          CompassDirection.NORTHEAST,
          CompassDirection.EAST,
          CompassDirection.SOUTHEAST,
          CompassDirection.SOUTH)
      Anchor.TOPLEFT -> return setOf(
          CompassDirection.EAST,
          CompassDirection.SOUTHEAST,
          CompassDirection.SOUTH)
      Anchor.TOP -> return setOf(
          CompassDirection.EAST,
          CompassDirection.SOUTHEAST,
          CompassDirection.SOUTH,
          CompassDirection.SOUTHWEST,
          CompassDirection.WEST)
      Anchor.TOPRIGHT -> return setOf(
          CompassDirection.SOUTH,
          CompassDirection.SOUTHWEST,
          CompassDirection.WEST)
      Anchor.RIGHT -> return setOf(
          CompassDirection.SOUTH,
          CompassDirection.SOUTHWEST,
          CompassDirection.WEST,
          CompassDirection.NORTHWEST,
          CompassDirection.NORTH)
      Anchor.BOTTOMRIGHT -> return setOf(
          CompassDirection.WEST,
          CompassDirection.NORTHWEST,
          CompassDirection.NORTH)
      Anchor.BOTTOM -> return setOf(
          CompassDirection.WEST,
          CompassDirection.NORTHWEST,
          CompassDirection.NORTH,
          CompassDirection.NORTHEAST,
          CompassDirection.EAST)
    }
  }

  val cardinalDirections: Map<CardinalDirection, Set<CompassDirection>>
    get() = mapOf(
        CardinalDirection.NORTH to setOf(
            CompassDirection.NORTHWEST,
            CompassDirection.NORTH,
            CompassDirection.NORTHEAST),
        CardinalDirection.EAST to setOf(
            CompassDirection.NORTHEAST,
            CompassDirection.EAST,
            CompassDirection.SOUTHEAST),
        CardinalDirection.SOUTH to setOf(
            CompassDirection.SOUTHEAST,
            CompassDirection.SOUTH,
            CompassDirection.SOUTHWEST),
        CardinalDirection.WEST to setOf(
            CompassDirection.SOUTHWEST,
            CompassDirection.WEST,
            CompassDirection.NORTHWEST)
    )
}



fun getAnchor(x: Int, y: Int, xMin: Int, xMax: Int, yMin:Int, yMax: Int) : Anchor {
  return if(x == xMin && y == yMin) Anchor.BOTTOMLEFT else
    if(x == xMax && y == yMax) Anchor.TOPRIGHT else
      if(x == xMin && y == yMax) Anchor.TOPLEFT else
        if(x == xMax && y == yMin) Anchor.BOTTOMRIGHT else
          if(x == xMin && y != yMin && y != yMax) Anchor.LEFT else
            if(x == xMax && y != yMin && y != yMax) Anchor.RIGHT else
              if(y == yMin && x != xMin && x != xMax) Anchor.BOTTOM else
                if(y == yMax && x != xMin && x != xMax) Anchor.TOP else Anchor.CENTER
}

fun MutableMap<Coordinate, Node<Coordinate, MapRelation>>.getNode(x: Int, y: Int, type: Int = 0): Node<Coordinate, MapRelation> {
  val coordinate = getCoord(x,y,type)
  var node = this[coordinate]
  if(node == null) {
    node = Node(coordinate)
    this[coordinate] = node
  }
  return node
}

fun getCoord(x: Int, y: Int, type: Int = 0) : Coordinate {
  return MapStuff.coordinateCache.getCoord(x,y,type)
}

fun MutableSet<Coordinate>.getCoord(x: Int, y: Int, type: Int = 0) : Coordinate {
  var coordinate = this.firstOrNull { it.x == x && it.y == y && it.type == type  }
  if(coordinate == null) {
    coordinate = Coordinate(x,y, type)
    this.add(coordinate)
  }
  return coordinate
}


object MapBuilder {

	fun addPortals(graph: Graph<Coordinate, MapRelation>) {
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

		for(node in graph.nodes) {
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

				val targetNode = possibleTargets.elementAt(MathUtils.random(0, possibleTargets.count() - 1))

				outNode.addRelation(MapStuff.portalRelation[direction]!!, targetNode)
				inMap[targetDirection]!!.remove(targetNode)
			}
		}
	}

  fun createWorld(xOffset: Int = 0, yOffset: Int = 0, width: Int = 10, height: Int = 10) : Graph<Coordinate, MapRelation> {
    val nodes = mutableMapOf<Coordinate, Node<Coordinate, MapRelation>>()
    val xMin = xOffset
    val xMax = xOffset + width - 1
    val yMin = yOffset
    val yMax = yOffset + height - 1
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

    return graph
  }
}