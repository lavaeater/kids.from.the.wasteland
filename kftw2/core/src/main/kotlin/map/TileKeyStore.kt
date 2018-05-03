package map

import com.lavaeater.kftw.map.TileKey

class TileKeyStore() {
    /*
    We want all TileKeys to exist once and only once.

    I believe this will improve performance. I might be wrong, though.
     */

    private val maxTiles = 1000000
    private val baseOffset = 500000

    private val maxX = baseOffset
    private val minX = maxX - maxTiles
    private val maxY = baseOffset
    private val minY = maxY - maxTiles

    private val tileKeys = Array(maxTiles, {x -> kotlin.arrayOfNulls<TileKey>(maxTiles)})
    init {
        for(x in minX..maxX)
            for(y in minY..maxY) {
                tileKeys[getArrayIndex(x)][getArrayIndex(y)] = TileKey(x,y)
            }
    }

    fun getArrayIndex(i:Int) : Int {
        return i + baseOffset
    }

    fun tileKey(x:Int, y:Int) : TileKey {
        return tileKeys[getArrayIndex(x)][getArrayIndex(y)]!!
    }
}