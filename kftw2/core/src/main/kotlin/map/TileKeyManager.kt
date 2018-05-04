package map

import com.lavaeater.kftw.map.TileKey
import kotlin.math.absoluteValue

class TileKeyManager() {
    /*
    We want all TileKeys to exist once and only once.

    I believe this will improve performance. I might be wrong, though.

    If the number of tiles is five, we should have these values:

    -2,-1,0,1,2

    so, lower bound is max - (max/2).roundToInt
    upper bound is...

    BaseOffset should be this particular stores key offset, not something else
     */

    val chunkSize = 10000
    val upperBound = chunkSize - 1
    val tileKeyStores = mutableMapOf<TileStoreKey, TileKeyStore>()

    fun getKeyFor(x:Int, y:Int):TileStoreKey {
        /*
        0 = 0..9999 = 0 to chunksize -1
         */
        val xBase = chunkSize * (x.rem(upperBound))
        val yBase = chunkSize * (y.rem(upperBound))

//        val xBase = chunkSize * (x.rem(chunkSize) -x) + if(x < 0) -(chunkSize + 1) else 0
  //      val yBase= chunkSize * (y.rem(chunkSize) - y) + if(y < 0) -(chunkSize + 1) else 0

        return TileStoreKey(xBase, xBase + upperBound, yBase, yBase + upperBound)
    }

    fun tileKey(x:Int, y:Int) : TileKey {
        return TileKey(x,y)
    }
}

data class TileStoreKey(val lowerBoundX: Int, val upperBoundX: Int, val lowerBoundY: Int, val upperBoundY: Int)
class TileKeyStore(lowerBoundX: Int, val columns: Int, lowerBoundY: Int, val rows: Int) {
    val upperBoundX = lowerBoundX + columns - 1
    val upperBoundY = lowerBoundY + rows - 1
    val key = TileStoreKey(lowerBoundX, upperBoundX, lowerBoundY, upperBoundY)

    val offsetX = lowerBoundX.absoluteValue
    val offsetY = lowerBoundY.absoluteValue
    val tileKeys = Array(columns, { _ -> arrayOfNulls<TileKey>(rows)})

    fun getXIndex(x:Int):Int {
        return x + offsetX
    }

    fun getYIndex(y:Int):Int {
        return y + offsetY
    }

    fun tileKey(x:Int, y:Int): TileKey {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        if(tileKeys[xIndex][yIndex] == null)
            tileKeys[xIndex][yIndex] = TileKey(x,y)

        return tileKeys[xIndex][yIndex]!!
    }

}
