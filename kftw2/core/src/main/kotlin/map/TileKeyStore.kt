package map

import com.lavaeater.kftw.map.TileKey

class TileKeyStore(lowerBound:Int = -500000, upperBound:Int = 500000) {
    /*
    We want all TileKeys to exist once and only once.

    I believe this will improve performance. I might be wrong, though.

    If the number of tiles is five, we should have these values:

    -2,-1,0,1,2

    so, lower bound is max - (max/2).roundToInt
    upper bound is...

    BaseOffset should be this particular stores key offset, not something else
     */
    private val numberOfTiles = (lowerBound..upperBound).count()
    private val baseOffset = kotlin.math.abs(lowerBound)

    private val tileKeys = Array(numberOfTiles, { _ -> kotlin.arrayOfNulls<TileKey>(numberOfTiles)})
    init {
//        for(x in lowerBound..upperBound)
//            for(y in lowerBound..upperBound) {
//                tileKeys[getArrayIndex(x)][getArrayIndex(y)] = TileKey(x, y)
//            }
    }

    fun getArrayIndex(i:Int) : Int {
        return i + baseOffset
    }

    fun tileKey(x:Int, y:Int) : TileKey {
        val xIndex = getArrayIndex(x)
        val yIndex = getArrayIndex(y)
        assert(xIndex in 0..(numberOfTiles - 1))
        assert(yIndex in 0..(numberOfTiles - 1))
        if(tileKeys[xIndex][yIndex] == null)
            tileKeys[xIndex][yIndex] = TileKey(x,y)

        return tileKeys[xIndex][yIndex]!!
    }
}