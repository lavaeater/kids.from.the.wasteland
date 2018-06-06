import map.TileStore
import org.junit.Test
import kotlin.test.assertEquals

class FlatTileStoreTests {
    @Test
    fun testLowerBoundZeroWithZeroValue() {
        val tileKeyStore = TileStore(0,10, 0, 10, emptyArray())
        val xIndex = tileKeyStore.getXIndex(0)
        assertEquals(0, xIndex)
    }
}

