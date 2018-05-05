package map

import com.badlogic.gdx.graphics.g2d.Sprite
import com.lavaeater.Assets
import com.lavaeater.kftw.map.Tile
import com.lavaeater.kftw.map.TileInstance
import com.lavaeater.kftw.map.TileKey

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

class TileManager(val chunkSize:Int = 10000) {
    val upperBound = chunkSize - 1
    private val tileStores = mutableSetOf<TileStore>()

    fun getLowerBound(i: Int): Int {
        if(i < 0) {
            return ((i + 1) / chunkSize) * chunkSize - chunkSize
        }

        return (i / chunkSize) * chunkSize
    }

    fun getTileStore(x:Int, y:Int) : TileStore {
        val lowerBoundX = getLowerBound(x)
        val lowerBoundY = getLowerBound(y)
        return getTileStoreLowerBounds(lowerBoundX,lowerBoundY)
    }

    fun getTileStoreLowerBounds(lX:Int, lY:Int) : TileStore {
        var store = tileStores.firstOrNull {
            lX in it.xBounds &&
                    lY in it.yBounds }
        if(store == null) {
            store = TileStore(lX, chunkSize, lY, chunkSize)
            tileStores.add(store)
        }
        return store
    }

    fun getTile(x:Int, y:Int) : TileInstance {
        val store = getTileStore(x,y)
        return store.getTile(x,y)!!
    }

    fun putTile(x:Int, y:Int, tile:TileInstance) {
        val store = getTileStore(x,y)
        store.putTile(x,y, tile)
    }

    fun putTiles(tilesToPut: Map<TileKey, Tile>) {
        tilesToPut.map { putTile(it.key.x, it.key.y, it.value.getInstance()) }
    }

    fun getTiles(xBounds:IntRange, yBounds:IntRange) : Array<Array<TileInstance>> {

        //This is a for loop. This gets the renderable map
        //To optimize, we should have all stores ready, but that's unnecesarry
        //We just get the first store and get a new one if needed!

        var currentStore : TileStore = getTileStore(xBounds.start, yBounds.start)
        val returnArray  = Array(xBounds.count(), { x -> Array(yBounds.count(), { y ->

            val actualX = xBounds.start + x
            val actualY = yBounds.start + y

            if(actualX !in currentStore.xBounds || actualY !in currentStore.yBounds) {
                currentStore = getTileStore(actualX,actualY)
            }
            return@Array currentStore.getTile(actualX,actualY)!!
        })})
        return returnArray
    }
}

fun Tile.getInstance(): TileInstance {
    return TileInstance(this.getSprite(), this.getExtraSprites())
}

fun Tile.getSprite() : Sprite {
    return Assets.sprites[this.tileType]!![this.subType]!!
}

fun Tile.getExtraSprites() : Array<Sprite> {
    if (Assets.codeToExtraTiles.containsKey(this.shortCode))
        return Assets.codeToExtraTiles[this.shortCode]!!.toTypedArray()
    return emptyArray()
}

class TileStore(val lowerBoundX: Int, val columns: Int, val lowerBoundY: Int, val rows: Int, val tiles: Array<Array<TileInstance?>> = Array(columns, {_ -> arrayOfNulls<TileInstance>(rows)})) {
    val upperBoundX = lowerBoundX + columns - 1
    val upperBoundY = lowerBoundY + rows - 1
    val xBounds :IntRange = lowerBoundX..upperBoundX
    val yBounds : IntRange = lowerBoundY..upperBoundY

    private val offsetX = lowerBoundX
    private val offsetY = lowerBoundY

    fun getXIndex(x:Int):Int {
        return x - offsetX
    }

    fun getYIndex(y:Int):Int {
        return y - offsetY
    }

    fun getTile(x:Int, y:Int): TileInstance? {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)

        return tiles[xIndex][yIndex]
    }

    fun getTiles(xRange:IntRange, yRange:IntRange) : Array<Array<TileInstance>> {
        if(xRange.start in xBounds &&
                xRange.endInclusive in xBounds &&
                yRange.start in yBounds &&
                        yRange.endInclusive in yBounds) {
            return xRange.map { column -> yRange.map { row -> tiles[column][row]!! } }.toTypedArray()
        }
        return emptyArray()
    }

    fun putTile(x: Int, y: Int, tile: TileInstance) {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        tiles[xIndex][yIndex] = tile
    }
}
