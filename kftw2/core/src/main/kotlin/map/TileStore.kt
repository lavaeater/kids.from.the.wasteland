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

    abstract val allTiles : Array<TileInstance>
}

class TileStore(lowerBoundX: Int, columns: Int, lowerBoundY: Int, rows: Int, val tiles: Array<Array<TileInstance>>) : TileStoreBase(lowerBoundX, columns, lowerBoundY, rows) {

    override val allTiles: Array<TileInstance> get() = emptyArray<TileInstance>()

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

class FlatTileStore(lowerBoundX: Int,
                    columns: Int,
                    lowerBoundY: Int,
                    rows: Int,
                    tiles: Array<TileInstance>) :
    TileStoreBase(lowerBoundX, columns, lowerBoundY, rows) {
    val size = columns * rows
    val flatTiles = tiles

    override val allTiles: Array<TileInstance>
        get() = flatTiles

    override fun getTile(x: Int, y: Int): TileInstance {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)

        val actualIndex = xIndex  + (yIndex * columns) //column x at row y
        return flatTiles[actualIndex]
    }

    override fun putTile(x: Int, y: Int, tile: TileInstance) {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)

      //y equals row. so for every y we must add that number of columns

        val actualIndex = xIndex + (yIndex * columns) //column x at row y
        flatTiles[actualIndex] = tile
    }
}