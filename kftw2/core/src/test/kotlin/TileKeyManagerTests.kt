import map.TileKeyManager
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyManagerTests {
    val tileKeyManager = TileKeyManager()

    @Test
    fun testZero() {
        var x = 0
        var y = 0

        var key = tileKeyManager.getKeyFor(x,y)

        assertEquals(0, key.lowerBoundX)
        assertEquals(tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(0, key.lowerBoundY)
        assertEquals(tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testChunkSize() {
        val x = tileKeyManager.chunkSize
        val y = tileKeyManager.chunkSize

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(tileKeyManager.chunkSize, key.lowerBoundX)
        assertEquals(tileKeyManager.upperBound  + tileKeyManager.chunkSize, key.upperBoundX)
        assertEquals(tileKeyManager.chunkSize, key.lowerBoundY)
        assertEquals(tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testChunkSizeMinusOne() {
        var x = tileKeyManager.chunkSize - 1
        var y = tileKeyManager.chunkSize - 1

        var key = tileKeyManager.getKeyFor(x, y)
        assertEquals(0, key.lowerBoundX)
        assertEquals(tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(0, key.lowerBoundY)
        assertEquals(tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeOne() {
        var x = -1
        var y = -1

        var key = tileKeyManager.getKeyFor(x,y)

        key = tileKeyManager.getKeyFor(x, y)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeChunkSize() {
        var x = -tileKeyManager.chunkSize
        var y = -tileKeyManager.chunkSize

        var key = tileKeyManager.getKeyFor(x,y)

        key = tileKeyManager.getKeyFor(x, y)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeChunkSizeMinusOne() {
        var x = -tileKeyManager.chunkSize - 1
        var y = -tileKeyManager.chunkSize - 1

        var key = tileKeyManager.getKeyFor(x,y)

        key = tileKeyManager.getKeyFor(x, y)
        assertEquals(-tileKeyManager.chunkSize * 2, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize * 2, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testOneNegativeOnePositive() {
        var x = tileKeyManager.chunkSize * 2
        var y = -tileKeyManager.chunkSize - 1 * 2

        var key = tileKeyManager.getKeyFor(x,y)

        key = tileKeyManager.getKeyFor(x, y)
        assertEquals(tileKeyManager.chunkSize * 2, key.lowerBoundX)
        assertEquals(tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize * 3, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize * 3 + tileKeyManager.upperBound, key.upperBoundY)
    }
}