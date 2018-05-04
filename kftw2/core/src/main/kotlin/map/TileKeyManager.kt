package map

import com.lavaeater.kftw.map.TileKey
import kotlin.math.absoluteValue

class TileKeyManager(val chunkSize:Int = 10000) {

    /*
    This is starting to look like the keyStore could handle all
    the map tiles in actuality and not go through the trouble
    of managing a bunch of keys to and fro, we'll look into that as
    time goes by
     */

    val upperBound = chunkSize - 1
    private val tileKeyStores = mutableMapOf<TileStoreKey, TileKeyStore>()
    private val tileKeys = mutableSetOf<TileKeyStore>()

    private val tileStoreKeys = mutableSetOf<TileStoreKey>()

    fun getLowerBound(i: Int): Int {
        if(i < 0) {
            return ((i + 1) / chunkSize) * chunkSize - chunkSize
        }

        return (i / chunkSize) * chunkSize
    }

    fun getKeyFor(x:Int, y:Int):TileStoreKey {
        val lX = getLowerBound(x)
        val lY = getLowerBound(y)
        val uX = lX + upperBound
        val uY = lY + upperBound

        var key = tileStoreKeys.firstOrNull {
            it.lowerBoundX == lX &&
            it.upperBoundX == uX &&
            it.lowerBoundY == lY &&
            it.upperBoundY == uY}

        if(key == null) {
            key = TileStoreKey(lX,uX,lY,uY)
            tileStoreKeys.add(key)
        }
        return key
    }

    fun tileKey(x:Int, y:Int) : TileKey {

        val lowerBoundX = getLowerBound(x)
        val lowerBoundY = getLowerBound(y)
        val upperBoundX = lowerBoundX + upperBound
        val upperBoundY = lowerBoundY + upperBound

        var store = tileKeys.firstOrNull {
                it.lowerBoundX == lowerBoundX &&
                it.upperBoundX == upperBoundX &&
                it.lowerBoundY == lowerBoundY &&
                it.upperBoundY == upperBoundY }
        if(store == null) {
            store = TileKeyStore(lowerBoundX, chunkSize, lowerBoundY, chunkSize)
            tileKeys.add(store)
        }

        return store.tileKey(x,y)
    }
}

data class TileStoreKey(val lowerBoundX: Int, val upperBoundX: Int, val lowerBoundY: Int, val upperBoundY: Int)
class TileKeyStore(val lowerBoundX: Int, val columns: Int, val lowerBoundY: Int, val rows: Int) {
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
