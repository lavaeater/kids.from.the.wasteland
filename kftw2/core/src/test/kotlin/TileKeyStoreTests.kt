import com.lavaeater.kftw.map.TileKey
import map.TileKeyStore
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyStoreTests {
    @Test
    fun testArrayIndex() {
        var tileKeyStore = TileKeyStore(-5,10, -5, 10)
        var index = tileKeyStore.getXIndex(0)
        assertEquals(5, index)
        index = tileKeyStore.getXIndex(-5)
        assertEquals(0,index)
        index = tileKeyStore.getYIndex(0)
        assertEquals(5, index)
        index = tileKeyStore.getYIndex(-5)
        assertEquals(0,index)
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

