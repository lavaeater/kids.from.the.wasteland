package map

import com.badlogic.gdx.math.MathUtils

class PerlinNoiseTileGenerator(private val terrainOptions: TerrainOptions = TerrainOptions()) : TileGenerator {
    override fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {
        val tiles = Array(
            xBounds.count()
        ) { x ->
            Array(yBounds.count()
            ) { y ->
                generateTile(
                    xBounds.elementAt(x),
                    yBounds.elementAt(y)) }
        }

        /*
        The extra sprite functionality must be adressed here, I guess? How do we manage
        the edges of the bigger map?

        If we can't find the neighbourtiles, we just skip it and flag the tile
        as needing neighbours - then we can check each tile when displaying and fix them then!

        Checking a true-false is fast, I imagine.
         */

        //This is like ordo or something
        for ((x, column) in tiles.withIndex())
            for ((y, _) in column.withIndex()) {

                val tempTile = fixNeighbours(tiles[x][y], x, y, tiles)

                tiles[x][y] = tempTile
            }

        return Array(xBounds.count()
        ) { column ->
            Array(yBounds.count()
            ) { row ->
                tiles[column][row]
                    .getInstance(
                        xBounds.elementAt(column),
                        yBounds.elementAt(row))
            }
        }
    }

    private fun generateTile(x: Int, y: Int): Tile {
        val nX = x / MapService.scale
        val nY = y / MapService.scale

        val priority = getTilePriorityFromNoise(nX, nY, x, y)
        val tileType = MapService.terrains[priority]!!
        val code = MapService.shortTerrains[priority]!!
        val subType = "center${MathUtils.random.nextInt(3) + 1}"
        return UsedTiles.tileFor(priority, tileType, subType, code)
    }

}