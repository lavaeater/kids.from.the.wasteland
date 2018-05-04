import com.lavaeater.kftw.map.TileKey
import map.TileKeyManager
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyManagerTests {
    val chunkSize = 5
    val tileKeyManager = TileKeyManager(chunkSize)
    val resultMap = mapOf(-20..-16 to -20,
            -15..-11 to -15,
            -10..-6 to -10,
            -5..-1 to -5,
            0..4 to 0,
    5..9 to 5,
    10..14 to 10,
    15..19 to 15,
    20..20 to 20)

    @Test
    fun getLowerBound_NegativeValues() {

        for(i in -20..-1) {
            val resultKey = resultMap.keys.first { i in it }
            val actualValue = tileKeyManager.getLowerBound(i)
            assertEquals(resultMap[resultKey], actualValue, "$i")
        }
    }

    @Test
    fun getLowerBound_PositiveValues() {
        for(i in 0..20) {
            val resultKey = resultMap.keys.first { i in it }
            val actualValue = tileKeyManager.getLowerBound(i)
            assertEquals(resultMap[resultKey]!!, actualValue, "$i")
        }
    }

    @Test
    fun testZero() {
        val x = 0
        val y = 0

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    fun getExpectedLowerBound(i:Int):Int {
        return resultMap[resultMap.keys.first { i in it }]!!
    }

    fun getExpectedUpperBound(i:Int):Int {
        return getExpectedLowerBound(i) + tileKeyManager.upperBound
    }

    @Test
    fun testOne() {
        val x = 1
        val y = 1

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testChunkSize() {
        val x = tileKeyManager.chunkSize
        val y = tileKeyManager.chunkSize

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testChunkSizeMinusOne() {
        val x = tileKeyManager.chunkSize - 1
        val y = tileKeyManager.chunkSize - 1

        val key = tileKeyManager.getKeyFor(x, y)
        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testNegativeOne() {
        val x = -1
        val y = -1

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testNegativeChunkSize() {
        val x = -tileKeyManager.chunkSize
        val y = -tileKeyManager.chunkSize

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testUpperBound() {
        val x = tileKeyManager.upperBound
        val y = tileKeyManager.upperBound

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testNegativeUpperBound() {
        val x = -tileKeyManager.upperBound
        val y = -tileKeyManager.upperBound

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testNegativeChunkSizeMinusOne() {
        val x = -tileKeyManager.chunkSize - 1
        val y = -tileKeyManager.chunkSize - 1

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testOneNegativeOnePositive() {
        val x = tileKeyManager.chunkSize * 2
        val y = -tileKeyManager.chunkSize - 1 * 2

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(getExpectedLowerBound(x), key.lowerBoundX, "$x, $y")
        assertEquals(getExpectedUpperBound(x), key.upperBoundX,"$x, $y")
        assertEquals(getExpectedLowerBound(y), key.lowerBoundY,"$x, $y")
        assertEquals(getExpectedUpperBound(y), key.upperBoundY,"$x, $y")
    }

    @Test
    fun testGetTileForZero() {
        val x = 0
        val y = 0

        val expectedKey = TileKey(0,0)
        val actualKey = tileKeyManager.tileKey(x,y)

        assertEquals(expectedKey, actualKey)
    }

    @Test
    fun testGetTileForChunkSize() {
        val x = tileKeyManager.chunkSize
        val y = tileKeyManager.chunkSize

        val expectedKey = TileKey(x,y)
        val actualKey = tileKeyManager.tileKey(x,y)

        assertEquals(expectedKey, actualKey)
    }

    @Test
    fun testGetTileForNegativeChunkSize() {
        val x = -tileKeyManager.chunkSize
        val y = -tileKeyManager.chunkSize

        val expectedKey = TileKey(x,y)
        val actualKey = tileKeyManager.tileKey(x,y)

        assertEquals(expectedKey, actualKey)
    }

    @Test
    fun testGetMinus9999() {
        val x = -9999
        val y = -9999

        val expectedKey = TileKey(x,y)
        val actualKey = tileKeyManager.tileKey(x,y)

        assertEquals(expectedKey, actualKey)

    }
}