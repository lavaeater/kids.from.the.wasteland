import com.lavaeater.kftw.map.TileKey
import map.TileKeyStore
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyStoreTests {
    @Test
    fun testLowerBoundZeroWithZeroValue() {
        val tileKeyStore = TileKeyStore(0,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(0)
        assertEquals(0, xIndex)
    }

    @Test
    fun testLowerBoundZeroWithMaxXValue() {
        val tileKeyStore = TileKeyStore(0,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(9)
        assertEquals(9, xIndex)
    }

    @Test
    fun testLowerBoundTenWithTenValue() {
        val tileKeyStore = TileKeyStore(10,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(10)
        assertEquals(0, xIndex)
    }

    @Test
    fun testLowerBoundTenWithTwentyValue() {
        val tileKeyStore = TileKeyStore(10,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(20)
        assertEquals(10, xIndex)
    }

    @Test
    fun testLowerBoundNegativeTenWithNegativeOne() {
        val tileKeyStore = TileKeyStore(-10,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(-1)
        assertEquals(9, xIndex)
    }

    @Test
    fun testLowerBoundNegativeTenWithNegativeTen() {
        val tileKeyStore = TileKeyStore(-10,10, 0, 10)
        val xIndex = tileKeyStore.getXIndex(-10)
        assertEquals(0, xIndex)
    }

    @Test
    fun testGetTileKeyGetsCorrectValue() {
        val tileKeyStore = TileKeyStore(-5,10, -5, 10)
        var expectedKey = TileKey(-5,-5)
        var key = tileKeyStore.tileKey(-5,-5)
        assertEquals(expectedKey, key)

        expectedKey = TileKey(4,4)
        key = tileKeyStore.tileKey(4,4)
        assertEquals(expectedKey, key)

        expectedKey = TileKey(0,0)
        key = tileKeyStore.tileKey(0,0)
        assertEquals(expectedKey, key)
    }

    @Test
    fun testCountOfKeysIsCorrect() {
        val tileKeyStore = TileKeyStore(-5,10, -5, 10)
        assertEquals(10, tileKeyStore.columns)
        assertEquals(10, tileKeyStore.rows)
    }

    @Test
    fun testUpperBoundIsCorrect() {
        val tileKeyStore = TileKeyStore(-5,10, -5, 10)
        assertEquals(4, tileKeyStore.upperBoundX)
        assertEquals(4, tileKeyStore.upperBoundY)
    }
}

