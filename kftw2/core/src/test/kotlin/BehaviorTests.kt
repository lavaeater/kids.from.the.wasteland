
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.behaviortree.NodeStatus
import com.lavaeater.kftw.behaviortree.behaviorTree
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
	fun inBounds_IsCorrect() {
		val dungeon = Dungeon(10,10)
		assertTrue { dungeon.isAreaInBounds(2,2,4,4) }
		assertTrue { dungeon.isAreaInBounds(0,0,10,10) }
		assertFalse{ dungeon.isAreaInBounds(0,0,11,11) }
		assertFalse{ dungeon.isAreaInBounds(-1,-1,5,5) }
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
		val dungeon = Dungeon(24,24)


		val roomFit = Room(2,2,6,6)

		val roomDoesNotFit = Room(8,2,5,5)
		val roomDoesNotFit2 = Room(2,8,5,5)

		val canWe = dungeon.canWePlaceRoom(roomFit)

		assertTrue { canWe }

		dungeon.placeRoom(roomFit)

		val room2 = dungeon.canWePlaceRoom(roomDoesNotFit)

		val room3 = dungeon.canWePlaceRoom(roomDoesNotFit2)

		dungeon.placeRoom(roomDoesNotFit)

		dungeon.placeRoom(roomDoesNotFit2)

		println(dungeon)

		assertFalse { room2 }
		assertFalse { room3 }
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
	fun dungeonBuilder_dungeonInitializedOnlyOnce() {
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
	private val numberOfRoomsRange = 25..50
	var numberOfRoomsLeftToPlace = MathUtils.random(numberOfRoomsRange.start, numberOfRoomsRange.endInclusive)

	val roomSizeRange = 4..8
	val triesPerRoom = 10

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

	fun tryToPlaceRoom(room: Room) {
		currentRoomTries++
		if(dungeon.tryToPlaceRoom(room)) {
			resetRoom()
			currentRoomTries = 0
			numberOfRoomsLeftToPlace--
		} else if(currentRoomTries >= triesPerRoom){
			numberOfRoomsLeftToPlace--
		}
	}
}


data class Room(val x:Int, val y:Int, val width: Int, val height: Int) //maybe not necessary

data class Dungeon(val height:Int, val width: Int) {
	val mapStorage = IntArray(height * width) { 0 } //init all zero array for dungeon
	val yBounds = 0 until height
	val xBounds = 0 until width

	fun getArea(x:Int, y:Int, w:Int, h: Int) : IntArray {
		//check bounds?
		var startIndex = indexFor(x, y) //Hmm...

		return IntArray(w*h) {
			var currentIndex = startIndex + it / w
			mapStorage[currentIndex]
		}
	}

	fun indexFor(x: Int, y: Int):Int {

		//Start index is... every row is y * width big. DAng, that's it.

		return y * width + x
	}

	fun setValue(x: Int, y: Int, value: Int) {
		mapStorage[indexFor(x,y)] = value
	}

	fun getValue(x: Int, y: Int, value: Int) : Int {
		return mapStorage[indexFor(x, y)]
	}

	override fun toString(): String {
    return "width: $width, height: $height" + System.lineSeparator() + mapStorage.prettyPrint(width)
	}

	/**
	 * returns true if we place it
	 */
	fun tryToPlaceRoom(room:Room):Boolean {
		return if(canWePlaceRoom(room)) {
			placeRoom(room)
			true
		}
		else false
	}

	fun placeRoom(room:Room) {
		setArea(room.x, room.y, room.width, room.height, 1)
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
		return getArea(x,y, w, h).all { it == type }
	}

	fun canWePlaceRoom(room: Room, spacing: Int = 2) : Boolean {
		//Spacing is the number of space OUTSIDE a room that we want

		val x = room.x - spacing
		val y = room.y - spacing
		val w = room.width + 2 * spacing
		val h = room.height + 2 * spacing

		return (x > -1 && y > -1) && isAreaOfType(x,y,w, h, 0)
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