package map

interface ITileStore {
    val rows: Int
    val upperBoundX: Int
    val upperBoundY: Int
    val xBounds : IntRange
    val yBounds : IntRange
    fun getXIndex(x: Int): Int
    fun getYIndex(y: Int): Int
    fun getTile(x: Int, y: Int): TileInstance
    fun putTile(x: Int, y: Int, tile: TileInstance)
}

abstract class TileStoreBase(val lowerBoundX: Int, val columns: Int, val lowerBoundY: Int, override val rows: Int) : ITileStore {
    override val upperBoundX = lowerBoundX + columns - 1
    override val upperBoundY = lowerBoundY + rows - 1
    override val xBounds: IntRange = lowerBoundX..upperBoundX
    override val yBounds: IntRange = lowerBoundY..upperBoundY
    val offsetX = lowerBoundX
    val offsetY = lowerBoundY
    override fun getXIndex(x: Int): Int {
        return x - offsetX
    }

    override fun getYIndex(y: Int): Int {
        return y - offsetY
    }
}

class TileStore(lowerBoundX: Int, columns: Int, lowerBoundY: Int, rows: Int, val tiles: Array<Array<TileInstance>>) : TileStoreBase(lowerBoundX, columns, lowerBoundY, rows) {

    override fun getTile(x: Int, y: Int): TileInstance {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)

        return tiles[xIndex][yIndex]
    }

    override fun putTile(x: Int, y: Int, tile: TileInstance) {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        tiles[xIndex][yIndex] = tile
    }
}

class FlatTileStore(lowerBoundX: Int, columns: Int, lowerBoundY: Int, rows: Int, tiles: Array<Array<TileInstance>>) : TileStoreBase(lowerBoundX, columns, lowerBoundY, rows) {
    val flatTiles = tiles.flatten().toTypedArray() //Oooh

    fun getIndex(x:Int, y:Int): Int {
        //How does flatten actually flatten an array of arrays? Lets assume it works with
    }

    override fun getTile(x: Int, y: Int): TileInstance {
        //We need to calculate what absolute position in the array a certain coordinate has.
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putTile(x: Int, y: Int, tile: TileInstance) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}