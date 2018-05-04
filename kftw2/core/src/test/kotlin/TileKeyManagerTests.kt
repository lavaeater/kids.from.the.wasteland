import com.lavaeater.kftw.map.TileKey
import map.TileKeyManager
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyManagerTests {
    val tileKeyManager = TileKeyManager()

    @Test
    fun testZero() {
        val x = 0
        val y = 0

        val key = tileKeyManager.getKeyFor(x,y)

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
        val x = tileKeyManager.chunkSize - 1
        val y = tileKeyManager.chunkSize - 1

        val key = tileKeyManager.getKeyFor(x, y)
        assertEquals(0, key.lowerBoundX)
        assertEquals(tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(0, key.lowerBoundY)
        assertEquals(tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeOne() {
        val x = -1
        val y = -1

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeChunkSize() {
        val x = -tileKeyManager.chunkSize
        val y = -tileKeyManager.chunkSize

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testNegativeChunkSizeMinusOne() {
        val x = -tileKeyManager.chunkSize - 1
        val y = -tileKeyManager.chunkSize - 1

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(-tileKeyManager.chunkSize * 2, key.lowerBoundX)
        assertEquals(-tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize * 2, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundY)
    }

    @Test
    fun testOneNegativeOnePositive() {
        val x = tileKeyManager.chunkSize * 2
        val y = -tileKeyManager.chunkSize - 1 * 2

        val key = tileKeyManager.getKeyFor(x,y)

        assertEquals(tileKeyManager.chunkSize * 2, key.lowerBoundX)
        assertEquals(tileKeyManager.chunkSize * 2 + tileKeyManager.upperBound, key.upperBoundX)
        assertEquals(-tileKeyManager.chunkSize * 3, key.lowerBoundY)
        assertEquals(-tileKeyManager.chunkSize * 3 + tileKeyManager.upperBound, key.upperBoundY)
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
}