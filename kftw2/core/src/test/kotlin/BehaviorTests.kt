
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.behaviortree.NodeStatus
import com.lavaeater.kftw.behaviortree.behaviorTree
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BehaviorTests {

	companion object {
		@JvmStatic
		@BeforeClass
		fun beforeClass() {
		}
	}

	@Before
	fun before() {
	}

	@After
	fun after() {
	}

	@Test
	fun createDungeon() {
		val dungeon = Dungeon(100, 100)
//		println(dungeon)

		val someArea = dungeon.getArea(10, 10, 10, 10)
		println(someArea.size)

		println()
		println(someArea.prettyPrint(10))
	}

	@Test
	fun indexFor_isCorrect() {
		/*
		We have issues with the indexfor methods etc.

		They need robust tests.
		 */
		val dungeon = Dungeon(10,10)
		//Indexes are easy here!
		var x = 0
		var y = 0


	}

	@Test
	fun inBounds_IsCorrect() {
		val dungeon = Dungeon(10,10)

		assertTrue { dungeon.isAreaInBounds(0,0,10,10) }
		assertFalse{ dungeon.isAreaInBounds(0,0,11,11) }
		assertFalse{ dungeon.isAreaInBounds(-1,-1,12,12) }
	}

	@Test
	fun inBoundsTrue_getAreaWorks() {
		val dungeon = Dungeon(4,4)

		assertEquals(0, dungeon.xBounds.start)
		assertEquals(3, dungeon.xBounds.endInclusive)
		assertNotEquals(4, dungeon.xBounds.endInclusive)

		assertEquals(0, dungeon.yBounds.start)
		assertEquals(3, dungeon.xBounds.endInclusive)
		assertNotEquals(4, dungeon.xBounds.endInclusive)

		assertFalse { dungeon.isAreaInBounds(0,0,5,5) }

		assertFalse { dungeon.canWePlaceRoom(Room(0,0,5,5)) }
		assertTrue { dungeon.canWePlaceRoom(Room(0,0,4,4),0) }
		assertFalse { dungeon.canWePlaceRoom(Room(1,1,3,3),1) }
		assertTrue { dungeon.canWePlaceRoom(Room(1,1,2,2),1) }

		assertTrue { dungeon.tryToPlaceRoom(Room(0,0,4,4), 1, 0) }

		println(dungeon)

	}

	@Test
	fun startIndex_isCorrect() {
		val dungeon = Dungeon(10,10)
		assertEquals(0, dungeon.indexFor(0,0))
		assertEquals(1, dungeon.indexFor(1,0))
		assertEquals(10, dungeon.indexFor(0,1))
		assertEquals(11, dungeon.indexFor(1,1))
		assertEquals(19, dungeon.indexFor(9,1))
		assertEquals(20, dungeon.indexFor(0,2))
		assertEquals(21, dungeon.indexFor(1,2))
		assertEquals(29, dungeon.indexFor(9,2))
	}

	@Test
	fun getArea_returnsCorrectSize() {
		val dungeon = Dungeon(100,100)
		assertEquals(10000, dungeon.mapStorage.size)

		val area = dungeon.getArea(1, 1, 10, 10)

		assertEquals(100, area.size)
	}

	@Test
	fun setArea_getArea_worksProperly() {
		val dungeon = Dungeon(100,100)
		assertEquals(10000, dungeon.mapStorage.size)

		var area = dungeon.getArea(1, 1, 10, 10)

		assertEquals(100, area.size)
		println(area.prettyPrint(10))

		var x = 4
		var y = 4
		var w = 10
		var h = 10

		dungeon.setArea(x,y,w,h, 1)
		area = dungeon.getArea(x,y,w,h)
		println(area.prettyPrint(w))

		x = 3
		y = 3
		w = 12
		h = 12

		area = dungeon.getArea(x,y,w,h)
		println(area.prettyPrint(w))

		x = 4
		y = 4
		w = 10
		h = 10

		area = dungeon.getArea(x,y,w,h)
		println(area.prettyPrint(w))

		area = dungeon.getArea(0,0,100,100)
		println(area.prettyPrint(100))
		println(dungeon)
	}

	@Test
	fun setArea_isCorrect() {
		val dungeon = Dungeon(10,10)

		println(dungeon)

		dungeon.setArea(1, 1, 5, 5, 1)

		println()
		println(dungeon)
	}

	@Test
	fun canWe_returnsTrue() {
		val dungeon = Dungeon(10,10)

		//In the above example, a room with 2,2,6,6 should fit.

		println(dungeon)

		val canWe = dungeon.canWePlaceRoom(Room(2,2,6,6))

		assertTrue { canWe }
	}

	@Test
	fun canWe_returnsFalse() {
		var dungeon = Dungeon(4,4)


		var roomToPlace = Room(0,0,4,4)


		var canWe = dungeon.canWePlaceRoom(roomToPlace)

		assertFalse { canWe }

		dungeon = Dungeon(8,8) //Bounds = 0..7

		assertEquals(7, dungeon.xBounds.endInclusive)

		roomToPlace = Room(1,1,4,4)

		assertFalse { dungeon.canWePlaceRoom(roomToPlace, 2) }

		roomToPlace = Room(2,2, 4,4) //This room must fit

		assertTrue { dungeon.canWePlaceRoom(roomToPlace, 2) }

		dungeon.placeRoom(roomToPlace)

		assertTrue { dungeon.getArea(roomToPlace.x, roomToPlace.y, roomToPlace.width, roomToPlace.height).all { it == 1 } }

		println(dungeon)

		//Start over with bigger dungeon!
		dungeon = Dungeon(12,12)
		roomToPlace = Room(2,2,2,2)

		assertTrue { dungeon.tryToPlaceRoom(roomToPlace) }

		var area = dungeon.getArea(roomToPlace.x - 2, roomToPlace.y - 2, roomToPlace.width + 4, roomToPlace.height + 4)

		println(area.prettyPrint(roomToPlace.width + 4))

		//new room to the right
		roomToPlace = Room(4,2,2,2)

		assertFalse { dungeon.tryToPlaceRoom(roomToPlace, 2) }

		roomToPlace = Room(5,2,2,2)

		assertFalse { dungeon.tryToPlaceRoom(roomToPlace,2) }

		roomToPlace = Room(6,2,2,2)

		assertTrue{ dungeon.tryToPlaceRoom(roomToPlace, 2) }

		//Room below

		roomToPlace = Room(2,4,2,2)

		assertFalse { dungeon.tryToPlaceRoom(roomToPlace, 2) }

		roomToPlace = Room(2,5,2,2)

		assertFalse { dungeon.tryToPlaceRoom(roomToPlace,2) }

		roomToPlace = Room(2,6,2,2)

		assertTrue{ dungeon.tryToPlaceRoom(roomToPlace, 2) }

		println(dungeon)
	}

	@Test
	fun bt_tick_lastStatus_SUCCESS() {

		//Arrange
		var data = 0
		val bt = behaviorTree<Int> {
			name = "test"
			actionRoot {
				name = "always succeeds"
				blackBoard = data
				action = { NodeStatus.SUCCESS }
			}
		}
		//Act
		bt.tick(1L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.SUCCESS, bt.lastStatus)
	}

	@Test
	fun bt_inverter_inverts() {

		//Arrange
		val bt = behaviorTree<Int> {
			name = "test"
			inverterRoot {
				name = "always succeeds"
				addAction {
					name = "fail"
					blackBoard = 0
					action = { NodeStatus.FAILURE}
				}
			}
		}
		//Act
		bt.tick(1L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.SUCCESS, bt.lastStatus)
	}

	@Test
	fun bt_tick_interval_works() {
		//Arrange
		var data = 0
		val bt = behaviorTree<Int> {
			name = "test"
			interval = 3L
			actionRoot {
				name = "always succeeds"
				blackBoard = data
				action = { NodeStatus.SUCCESS }
			}
		}
		//Act
		bt.tick(2L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.NONE, bt.lastStatus)

		bt.tick(2L)
		assertEquals(NodeStatus.SUCCESS, bt.lastStatus)

		bt.tick(1L)
		assertEquals(NodeStatus.NONE, bt.lastStatus)
	}

	@Test
	fun bt_sequence_succeeds_lastStatus_success() {
		//Arrange
		var data = 0
		val bt = behaviorTree<Int> {
			name = "test"
			sequenceRoot {
				name = "sequence"
				addAction {
					name = "wut"
					blackBoard = data
					action = {
						data++
						NodeStatus.SUCCESS
					}
				}
				addAction {
					name = "wut"
					blackBoard = data
					action = {
						data++
						NodeStatus.SUCCESS
					}
				}
			}
		}
		//Act
		bt.tick(1L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.SUCCESS, bt.lastStatus)
		assertEquals(2, data)
	}

	@Test
	fun bt_sequence_fails_lastStatus_fail() {
		//Arrange
		var data = 0
		val bt = behaviorTree<Int> {
			name = "test"
			sequenceRoot {
				name = "sequence"
				addAction {
					name = "wut"
					blackBoard = data
					action = {
						data++
						NodeStatus.FAILURE
					}
				}
				addAction {
					name = "wut"
					blackBoard = data
					action = {
						data++
						NodeStatus.SUCCESS
					}
				}
			}
		}
		//Act
		bt.tick(1L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.FAILURE, bt.lastStatus)
		assertEquals(1, data)
	}

	@Test
	fun bt_selector_first_success_is_run() {
		//Arrange
		var data = 0
		val bt = behaviorTree<Int> {
			name = "tree"
			selectorRoot {
				name = "selector"
				addAction {
					name = "fail"
					blackBoard = data
					action = {
						data++
						NodeStatus.FAILURE } //Won't stop here
				}
				addAction {
					name = "success"
					blackBoard = data
					action = {
						data++
						NodeStatus.SUCCESS
					} //Won't continue to next one
				}
				addAction {
					name = "fail2"
					blackBoard = data
					action = {
						data++
						NodeStatus.SUCCESS
					}
				}
			}
		}
		//Act
		bt.tick(1L)
		//Assert
		assertTrue { bt.lastStatus == NodeStatus.SUCCESS }
		assertEquals(2, data)
	}

	@Test
	fun dungeonBuilder_buildADungeon() {
		//arrange
		val dungeonBuilder = DungeonBuilder()


				val bt = behaviorTree<DungeonBuilder> {
			name = "dungeonBuilder"
			selectorRoot {
				name = "rootselector"
				addAction {
					name = "create dungeon, run once"
					blackBoard = dungeonBuilder
					action = {
						if(it.dungeonInitialized)
							NodeStatus.FAILURE
						else {
							it.initializeDungeon(50..100)
							NodeStatus.SUCCESS
						}
					}
				}
				addSequence {
					/*
					WHen does this sequence fail?

					Well, it fails when it finally is
					DONE with adding rooms, this means that some criteria
					are fulfilled. So the FIRST action checks this condition!
					 */
					name = "add a bunch of rooms"
					addAction {
						name = "check status"
						blackBoard = dungeonBuilder
						action = {
							if(it.roomPlacingDone) NodeStatus.FAILURE else {
								NodeStatus.SUCCESS
							}
						}
					}
					addAction {
						name = "place a room"
						blackBoard = dungeonBuilder
						action = {
							if(it.needsRoom)
								it.createRandomRoom()

							it.tryToPlaceRoom(it.currentRoom)
							NodeStatus.SUCCESS
						}
					}
				}
			}
		}

		while (bt.lastStatus != NodeStatus.FAILURE) {
			bt.tick(1L)
		}

		println(dungeonBuilder.dungeon)
	}

	@Test
	fun getArea_returnsCorrectValues() {
		val dungeon = Dungeon(10,10)

		println(dungeon)

		dungeon.setArea(1, 1, 5, 5, 1)

		println()
		println(dungeon)

		val area = dungeon.getArea(1, 1, 5, 5)

		println(area.prettyPrint(5))

		assertEquals(25, area.size)
		assertTrue { area.all { it == 1 } }
	}
}

class DungeonBuilder {
	/*
	Just a bag of data and methods to help with building a
	dungeon using a behavior tree

	Lets just make all this params part of the object from the start,
	no need to put them in the constructor just yet.

		var currentRoomTries = 0
		var currentRoomIndex = minNumberOfRooms - 1
		var currentRoom = Room(0, 0, 1, 1)
		var dungeon = Dungeon(1,1)

	 */

	var dungeon = Dungeon(1,1)
	private val numberOfRoomsRange = 25..100
	var numberOfRoomsLeftToPlace = MathUtils.random(numberOfRoomsRange.start, numberOfRoomsRange.endInclusive)

	val roomSizeRange = 5..20
	val triesPerRoom = 20

	var currentRoom = Room(0,0,0,0)

	fun createRandomRoom() {
		//1. Create width and height for room
		val width = MathUtils.random(roomSizeRange.start, roomSizeRange.endInclusive)
		val height = MathUtils.random(roomSizeRange.start, roomSizeRange.endInclusive)
		//2. Find reasonable bounds for x and y:
		val x = MathUtils.random(1, dungeon.width - 1 - width)
		val y = MathUtils.random(1, dungeon.height - 1 - height)

		currentRoom = Room(x,y, width, height)
	}

	fun resetRoom() {
		currentRoom = Room(0,0,0,0)
	}

	val dungeonInitialized get() = dungeon.width != 1 && dungeon.height != 1
	val needsRoom get() = (currentRoom.width == 0 && currentRoom.height == 0) || currentRoomTries >= triesPerRoom

	val roomPlacingDone get() = numberOfRoomsLeftToPlace == 0

	fun initializeDungeon(sideRange: IntRange = 10..100) {
		if(!dungeonInitialized) {
			dungeon = Dungeon(
					MathUtils.random(sideRange.start, sideRange.endInclusive),
					MathUtils.random(sideRange.start, sideRange.endInclusive))
		}
	}

	private var currentRoomTries = 0

	fun tryToPlaceRoom(room: Room, code: Int = 1, spacing: Int = 4) {
		currentRoomTries++
		if(dungeon.tryToPlaceRoom(room, code, spacing)) {
			resetRoom()
			currentRoomTries = 0
			numberOfRoomsLeftToPlace--
		} else if(currentRoomTries >= triesPerRoom){
			numberOfRoomsLeftToPlace--
		}
	}
}


data class Room(val x:Int, val y:Int, val width: Int, val height: Int) //maybe not necessary

data class Dungeon(val width: Int, val height: Int) {
	val mapStorage = IntArray(height * width) { 0 } //init all zero array for dungeon
	val yBounds = 0 until height
	val xBounds = 0 until width

	fun getArea(x:Int, y:Int, w:Int, h: Int) : IntArray {
		//check bounds?

		/*
		This is so dumb. I am dumb.

		Every coordinate in the area we want correspond to an index of the larger map

		So... x = 2, y = 0 => startIndex 2
		x = 2, y = 1 =>

		NO, every index in the RESULTING array corresponds to a
		coordinate in the actual dungeon.

		So, if x = 2 and y = 2 then

		i = 0 => 2,2
		i = 1 => 3,2

		and so on until i > w. if w = 4

		i = 5 => 2,3

		 */

		return IntArray(w*h) {
			val currentX = x + it / (w -1)
			val currentY = y + it % w

			getValue(currentX, currentY)
		}
	}

	fun indexFor(x: Int, y: Int):Int {

		//Start index is... every row is y * width big. DAng, that's it.

		return y * width + x
	}

	fun setValue(x: Int, y: Int, value: Int) {
		mapStorage[indexFor(x,y)] = value
	}

	fun getValue(x: Int, y: Int) : Int {
		val index = indexFor(x,y)
		return mapStorage[index]
	}

	override fun toString(): String {
    return "width: $width, height: $height" + System.lineSeparator() + mapStorage.prettyPrint(width)
	}

	/**
	 * returns true if we place it
	 */
	fun tryToPlaceRoom(room:Room, code:Int = 1, spacing: Int = 2):Boolean {
		return if(canWePlaceRoom(room, spacing)) {
			placeRoom(room, code)
			true
		}
		else false
	}

	fun placeRoom(room:Room, code: Int = 1) {
		setArea(room.x, room.y, room.width, room.height, code)
	}

	fun setArea(x: Int, y: Int, w: Int, h: Int, value: Int) {
		if (!isAreaInBounds(x,y,w,h)) throw IndexOutOfBoundsException()

		var startIndex = indexFor(x,y)
		//then loop over h rows and set w to value! Otherwise wrong...

		for(row in 0 until h) {
			for(column in 0 until w) {
				val currentIndex = startIndex + column
				mapStorage[currentIndex] = value
			}
			startIndex += width
		}
	}

	fun isAreaOfType(x: Int, y: Int, w: Int, h: Int, type: Int) : Boolean {
		val area = getArea(x,y, w, h)
		return area.all { it == type }
	}

	fun canWePlaceRoom(room: Room, spacing: Int = 2) : Boolean {
		//Spacing is the number of space OUTSIDE a room that we want

		val x = room.x - spacing
		val y = room.y - spacing
		val w = room.width + 2 * spacing
		val h = room.height + 2 * spacing

		return isAreaInBounds(x, y, w, h) && isAreaOfType(x,y,w, h, 0)
	}

	fun isAreaInBounds(x: Int, y: Int, w: Int, h: Int): Boolean {
		return x in xBounds && y in yBounds && x + w - 1 in xBounds && y + h - 1 in yBounds
	}
}

fun IntArray.prettyPrint(width:Int) : String {
  val sb = StringBuilder()
  for (tile in this.withIndex()) {
    //Same row?
    if(tile.index % width == 0)
      sb.append(System.lineSeparator())

    sb.append(tile.value)
  }
  return sb.toString()
}