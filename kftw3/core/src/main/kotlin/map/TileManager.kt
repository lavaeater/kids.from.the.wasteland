package map

import Assets
import com.badlogic.gdx.math.MathUtils

class TileManager(val chunkSize:Int = 100) {
    private val upperBound = chunkSize - 1
    private val tileStores = mutableSetOf<TileStoreBase>()
    private val usedTiles = mutableMapOf<String, Tile>()

    private fun getLowerBound(i: Int): Int {
        if (i < 0) {
            return ((i + 1) / chunkSize) * chunkSize - chunkSize
        }

        return (i / chunkSize) * chunkSize
    }

    private fun getTileStore(x: Int, y: Int): ITileStore {
        val lowerBoundX = getLowerBound(x)
        val lowerBoundY = getLowerBound(y)
        return getTileStoreLowerBounds(lowerBoundX, lowerBoundY)
    }

    private fun getTileStoreLowerBounds(lX: Int, lY: Int): ITileStore {
        var store = tileStores.firstOrNull {
            lX in it.xBounds &&
                lY in it.yBounds
        }
        if (store == null) {
            store = FlatTileStore(
                lX,
                chunkSize,
                lY,
                chunkSize,
                generateTilesForRange(lX..(lX + upperBound), lY..(lY + upperBound)))
            tileStores.add(store)
        }
        return store
    }

    fun getTile(x: Int, y: Int): TileInstance {
        val store = getTileStore(x, y)
        return store.getTile(x, y)
    }

    private fun putTile(x: Int, y: Int, tile: TileInstance) {
        val store = getTileStore(x, y)
        store.putTile(x, y, tile)
    }

    fun getTilesFlat(xBounds: IntRange, yBounds: IntRange) : Array<TileInstance> {

        lateinit var currentTile: TileInstance
        var currentStore = getTileStore(xBounds.start, yBounds.start)
        val columns = xBounds.count()
        val rows = yBounds.count()
        val size = columns * rows
        var row = 0
        var column = 0
        return Array(size, { index ->

            val x = xBounds.start + column
            val y = yBounds.start + row

            if (x !in currentStore.xBounds || y !in currentStore.yBounds) {
                currentStore = getTileStore(x, y)
            }
            currentTile = currentStore.getTile(x, y)

            if (currentTile.tile.needsNeighbours) {
                currentTile = fixNeighbours(currentTile.tile, x, y).getInstance(x, y)
                putTile(x, y, currentTile)
            }

            column++
            if(column > xBounds.count() - 1) {
                column = 0
                row++
            }

            return@Array currentTile
        })
    }

    fun getTiles(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {

        //This is a for loop. This gets the renderable map
        //To optimize, we should have all stores ready, but that's unnecesarry
        //We just get the first store and get a new one if needed!
        lateinit var currentTile: TileInstance
        var currentStore = getTileStore(xBounds.start, yBounds.start)
        return Array(xBounds.count(), { x ->
            Array(yBounds.count(), { y ->

                val actualX = xBounds.start + x
                val actualY = yBounds.start + y

                if (actualX !in currentStore.xBounds || actualY !in currentStore.yBounds) {
                    currentStore = getTileStore(actualX, actualY)
                }
                currentTile = currentStore.getTile(actualX, actualY)

                //check for neighbours!
                if (currentTile.tile.needsNeighbours) {
                    currentTile = fixNeighbours(currentTile.tile, actualX, actualY).getInstance(actualX, actualY)
                    putTile(actualX, actualY, currentTile)
                }

                return@Array currentTile
            })
        })
    }

    private fun getOrNull(x: Int, y: Int, tiles: Array<Array<Tile>>? = null): Tile? {
        if (tiles == null) { //Use the manager to get the tile to check!
            return getTile(x, y).tile
        }

        val col = tiles.getOrNull(x)
        if (col != null) {
            val tile = col.getOrNull(y)
            if (tile != null) {
                return tile
            }
        }
        return null
    }

    private fun fixNeighbours(tile: Tile, x: Int, y: Int, tiles: Array<Array<Tile>>? = null): Tile {
        val tempTile = tile.copy()
        var needsNeighbours = false


        MapManager.neiborMap.keys.forEach { (offX, offY) ->
            var code = "b"
            val t = getOrNull(x + offX, y + offY, tiles)
            if (t != null) {
                code = MapManager.shortTerrains[t.priority]!!
            } else {
                needsNeighbours = true
            }
            tempTile.code += code
        }

        tempTile.needsNeighbours = needsNeighbours
        tempTile.shortCode = tempTile.code.toShortCode()

        val keyCode = tempTile.getKeyCode()

        if (!usedTiles.containsKey(keyCode)) {
            //Add this new tile to the tile storage!
            addEdgeSpritesForTile(tempTile.shortCode, tempTile.priority)
            usedTiles[keyCode] = tempTile
        }
        return usedTiles[keyCode]!!
    }

    private fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {
        val tiles = Array(xBounds.count(), { x -> Array(yBounds.count(), { y -> generateTile(xBounds.elementAt(x), yBounds.elementAt(y)) }) })

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

        return Array(xBounds.count(),
            { column ->
                Array(yBounds.count(),
                    { row ->
                        tiles[column][row]
                            .getInstance(
                                xBounds.elementAt(column),
                                yBounds.elementAt(row))
                    })
            })
    }

    private fun getDirectionFromIndex(index: Int): String {
        return when (index) {
            0 -> "north"
            1 -> "east"
            2 -> "south"
            3 -> "west"
            else -> "north"
        }
    }

    private fun addEdgeSpritesForTile(shortCode: String, priority: Int) {

        if (!MapManager.noExtraSprites.contains(shortCode) && !Assets.codeToExtraTiles.containsKey(shortCode)) {
            val tileC = shortCode.toCharArray()[0]
            val actualShortCode = shortCode.substring(1..4) //leave out the tile itself.
            val extraSprites = mutableListOf<Pair<String, String>>()
            val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

            for ((index, code) in actualShortCode.withIndex()) {
                if (code != 'b' && tileC != code && MapManager.shortTerrainPriority[code]!! > priority) {
                    if (extraSprites.any { it.first == MapManager.shortLongTerrains[code]!! }) {
                        //Evaluate for diffs etc
                        for (extraSprite in extraSprites.filter { it.first == MapManager.shortLongTerrains[code]!! }) {

                            /*
                      This type of tile exists, it might actually be relevant to
                      remove the existing one in favor of this one!
                       */
                            if (MapManager.weirdDirections.containsKey("${getDirectionFromIndex(index)}${extraSprite.second}")) {
                                //Modify existing one, making it weird!
                                extraSpritesToRemove.add(extraSprite)
                                extraSprites.add(Pair(extraSprite.first, MapManager.weirdDirections["${getDirectionFromIndex(index)}${extraSprite.second}"]!!))
                            } else {
                                extraSprites.add(Pair(MapManager.shortLongTerrains[code]!!, getDirectionFromIndex(index)))
                            }
                        }
                    } else {
                        //just add
                        extraSprites.add(Pair(MapManager.shortLongTerrains[code]!!, getDirectionFromIndex(index)))
                    }
                }
            }
            for (extraSprite in extraSpritesToRemove) {
                extraSprites.remove(extraSprite)
            }

            if (extraSprites.any())
                Assets.codeToExtraTiles[shortCode] = extraSprites.map { Assets.tileSprites[it.first]!![it.second]!! }
            else
                MapManager.noExtraSprites.add(shortCode)
        }
    }

    private fun generateTile(x: Int, y: Int): Tile {
        val nX = x / MapManager.scale
        val nY = y / MapManager.scale

        val priority = getTilePriorityFromNoise(nX, nY, x, y)
        val tileType = MapManager.terrains[priority]!!
        val code = MapManager.shortTerrains[priority]!!
        val subType = "center${MathUtils.random.nextInt(3) + 1}"
        val tileCode = "$priority$tileType$subType$code$code${true}" //Only temporary, actually
        if (!usedTiles.containsKey(tileCode))
            usedTiles[tileCode] = Tile(priority, tileType, subType, code, code)

        return usedTiles[tileCode]!!
    }
}