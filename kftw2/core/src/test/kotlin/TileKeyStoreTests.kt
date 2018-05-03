import com.lavaeater.kftw.map.TileKey
import map.TileKeyStore
import org.junit.Test
import kotlin.test.assertEquals

class TileKeyStoreTests {

    val tileKeyStore = TileKeyStore(-5,5)

    @Test
    fun testArrayIndex() {
        var index = tileKeyStore.getArrayIndex(0)
        assertEquals(5, index)
        index = tileKeyStore.getArrayIndex(-5)
        assertEquals(0,index)
    }

    @Test
    fun testGetTileKeyGetsCorrectValue() {
        var expectedKey = TileKey(-5,-5)
        var key = tileKeyStore.tileKey(-5,-5)
        assertEquals(expectedKey, key)
        expectedKey = TileKey(5,5)
        key = tileKeyStore.tileKey(5,5)
        assertEquals(expectedKey, key)
    }


}