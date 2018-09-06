
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
	fun getArea_returnsCorrectValues() {
		val dungeon = Dungeon(100,100)

		println(dungeon)

		dungeon.setArea(1, 1, 10, 10, 1)

		println()
		println(dungeon)

		val area = dungeon.getArea(1, 1, 10, 10)

		assertEquals(100, area.size)
		assertTrue { area.all { it == 1 } }
	}
}

class Dungeon(val height:Int, val width: Int) {
	val mapStorage = IntArray(height * width) { 0 } //init all zero array for dungeon
	val yBounds = 0 until height
	val xBounds = 0 until width

	fun getArea(x:Int, y:Int, w:Int, h: Int) : IntArray {
		//check bounds?


		val startIndex = indexFor(x, y) //Hmm...
		val endIndex = startIndex + w*h
		return mapStorage.sliceArray(startIndex until endIndex)
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

	fun setArea(x: Int, y: Int, w: Int, h: Int, value: Int) {
		if (!isAreaInBounds(x,y,w,h)) throw IndexOutOfBoundsException()

		val startIndex = indexFor(x,y)
		//then loop over h rows and set w to value! Otherwise wrong...

		for(row in 0 until h) {
			
		}


		val endIndex = startIndex + w * h
		for (index in startIndex until endIndex)
			mapStorage[index] = value
	}

	fun isAreaOfType(x: Int, y: Int, w: Int, h: Int, type: Int) : Boolean {
		return getArea(x,y, w, h).all { it == type }
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