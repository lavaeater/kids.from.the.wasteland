package map

class TileStore(val lowerBoundX: Int, val columns: Int, val lowerBoundY: Int, val rows: Int, val tiles: Array<Array<TileInstance>>) {
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

    fun getTile(x:Int, y:Int): TileInstance {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)

        return tiles[xIndex][yIndex]!!
    }

    fun putTile(x: Int, y: Int, tile: TileInstance) {
        val xIndex = getXIndex(x)
        val yIndex = getYIndex(y)
        tiles[xIndex][yIndex] = tile
    }
}