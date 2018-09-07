
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
		assertEquals(NodeStatus.SUCCESS, bt.LastStatus)
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
		assertEquals(NodeStatus.NONE, bt.LastStatus)

		bt.tick(2L)
		assertEquals(NodeStatus.SUCCESS, bt.LastStatus)

		bt.tick(1L)
		assertEquals(NodeStatus.NONE, bt.LastStatus)
	}

	@Test
	fun bt_sequence() {
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
			}
			actionRoot {
				name = "always succeeds"
				blackBoard = data
				action = { NodeStatus.SUCCESS }
			}
		}
		//Act
		bt.tick(2L) //We don't use intervals in tests so...

		//Assert
		assertEquals(NodeStatus.NONE, bt.LastStatus)

		bt.tick(2L)
		assertEquals(NodeStatus.SUCCESS, bt.LastStatus)

		bt.tick(1L)
		assertEquals(NodeStatus.NONE, bt.LastStatus)
	}

	@Test
	fun treeTesting() {
		val sideRange = 25..100
		var dungeonCreated = false

		val minNumberOfRooms = 3
		val numberOfRooms = minNumberOfRooms..MathUtils.random(4, 15)
		val roomSize = 2..5
		val triesPerRoom = 5
		var currentRoomTries = 0
		var currentRoomIndex = minNumberOfRooms - 1
		var currentRoom = Room(0, 0, 1, 1)
		var dungeon = Dungeon(1,1)




//		val bt = behaviorTree<Dungeon> {
//			name = "root"
//			rootNode = selector<Dungeon> {
//				name = "builddungeon"
//					addSequence {
//						name = "create dungeon"
//						addAction {
//							action = { if(dungeonCreated) NodeStatus.FAILURE else  {
//								dungeon = Dungeon(MathUtils.random(sideRange.start, sideRange.endInclusive),
//										MathUtils.random(sideRange.start, sideRange.endInclusive))
//								NodeStatus.SUCCESS
//							}}
//						}
//					}
//					addSequence {
//						name = "putrooms"
//						addAction {
//							name = "put a room"
//							blackBoard = dungeon
//							action = {
//								if(currentRoomTries == 0) {
//									val width = MathUtils.random(2, 5)
//									val height = MathUtils.random(2, 5)
//
//									val x = MathUtils.random(0, side - 1 - width)
//									val y = MathUtils.random(0, side - 1 - height)
//
//									currentRoom = Room(x, y, width, height)
//								}
//
//								if(it.tryToPlaceRoom(currentRoom)) {
//									NodeStatus.SUCCESS
//								} else if(currentRoomTries < triesPerRoom) {
//									currentRoomTries++
//										NodeStatus.RUNNING
//								} else {
//									currentRoomTries = 0
//									NodeStatus.FAILURE
//
//								}
//							}
//					}
//				}
//			}
//		}
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
    return mapStorage.prettyPrint(width)
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

	fun canWePlaceRoom(room: Room) : Boolean {
		return isAreaOfType(room.x, room.y, room.width, room.height, 1)
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