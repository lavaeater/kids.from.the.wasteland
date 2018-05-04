package map

import com.lavaeater.kftw.map.TileKey
import kotlin.math.absoluteValue

class TileKeyManager() {

    /*
    This is starting to look like the keyStore could handle all
    the map tiles in actuality and not go through the trouble
    of managing a bunch of keys to and fro, we'll look into that as
    time goes by
     */

    val chunkSize = 10000
    val upperBound = chunkSize - 1
    private val tileKeyStores = mutableMapOf<TileStoreKey, TileKeyStore>()

    fun getKeyFor(x:Int, y:Int):TileStoreKey {

        val xBase = chunkSize * (x.rem(upperBound))
        val yBase = chunkSize * (y.rem(upperBound))

        return TileStoreKey(xBase, xBase + upperBound, yBase, yBase + upperBound)
    }

    fun tileKey(x:Int, y:Int) : TileKey {
        val storeKey = getKeyFor(x,y)

        if(!tileKeyStores.containsKey(storeKey))
            tileKeyStores[storeKey] = TileKeyStore(storeKey.lowerBoundX, chunkSize, storeKey.lowerBoundY, chunkSize)

        return tileKeyStores[storeKey]!!.tileKey(x,y)
    }
}

data class TileStoreKey(val lowerBoundX: Int, val upperBoundX: Int, val lowerBoundY: Int, val upperBoundY: Int)
class TileKeyStore(lowerBoundX: Int, val columns: Int, lowerBoundY: Int, val rows: Int) {
    val upperBoundX = lowerBoundX + columns - 1
    val upperBoundY = lowerBoundY + rows - 1
    val key = TileStoreKey(lowerBoundX, upperBoundX, lowerBoundY, upperBoundY)

    private val offsetX = lowerBoundX
    private val offsetY = lowerBoundY
    private val tileKeys = Array(columns, { _ -> arrayOfNulls<TileKey>(rows)})

    fun getXIndex(x:Int):Int {
        return x - offsetX
    }

    fun getYIndex(y:Int):Int {
        return y - offsetY
    }

    fun tileKey(x:Int, y:Int): TileKey {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        if(tileKeys[xIndex][yIndex] == null)
            tileKeys[xIndex][yIndex] = TileKey(x,y)

        return tileKeys[xIndex][yIndex]!!
    }

}
