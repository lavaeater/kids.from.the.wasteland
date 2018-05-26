package map

import com.badlogic.gdx.math.MathUtils
import com.lavaeater.Assets
import com.lavaeater.kftw.map.*

class TileManager(val chunkSize:Int = 100) {
    val upperBound = chunkSize - 1
    private val tileStores = mutableSetOf<TileStore>()
    val usedTiles = mutableMapOf<String, Tile>()

    fun getLowerBound(i: Int): Int {
        if (i < 0) {
            return ((i + 1) / chunkSize) * chunkSize - chunkSize
        }

        return (i / chunkSize) * chunkSize
    }

    fun getTileStore(x: Int, y: Int): TileStore {
        val lowerBoundX = getLowerBound(x)
        val lowerBoundY = getLowerBound(y)
        return getTileStoreLowerBounds(lowerBoundX, lowerBoundY)
    }

    fun getTileStoreLowerBounds(lX: Int, lY: Int): TileStore {
        var store = tileStores.firstOrNull {
            lX in it.xBounds &&
                lY in it.yBounds
        }
        if (store == null) {
            store = TileStore(lX, chunkSize, lY, chunkSize, generateTilesForRange(lX..(lX + upperBound), lY..(lY + upperBound)))
            tileStores.add(store)
        }
        return store
    }

    fun getTile(x: Int, y: Int): TileInstance {
        val store = getTileStore(x, y)
        return store.getTile(x, y)!!
    }

    fun putTile(x: Int, y: Int, tile: TileInstance) {
        val store = getTileStore(x, y)
        store.putTile(x, y, tile)
    }

    fun getTiles(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {

        //This is a for loop. This gets the renderable map
        //To optimize, we should have all stores ready, but that's unnecesarry
        //We just get the first store and get a new one if needed!
        lateinit var currentTile: TileInstance
        var currentStore: TileStore = getTileStore(xBounds.start, yBounds.start)
        return Array(xBounds.count(), { x ->
            Array(yBounds.count(), { y ->

                val actualX = xBounds.start + x
                val actualY = yBounds.start + y

                if (actualX !in currentStore.xBounds || actualY !in currentStore.yBounds) {
                    currentStore = getTileStore(actualX, actualY)
                }
                currentTile = currentStore.getTile(actualX, actualY)!!

                //check for neighbours!
                if (currentTile.tile.needsNeighbours) {
                    currentTile = fixNeighbours(currentTile.tile, actualX, actualY).getInstance(actualX, actualY)
                    putTile(actualX, actualY, currentTile)
                }

                return@Array currentTile
            })
        })
    }

    fun getOrNull(x: Int, y: Int, tiles: Array<Array<Tile>>? = null): Tile? {
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

    fun fixNeighbours(tile: Tile, x: Int, y: Int, tiles: Array<Array<Tile>>? = null): Tile {
        val tempTile = tile.copy()
        var needsNeighbours = false


        MapManager.neiborMap.keys.forEach { (offX, offY) ->
            var code = "b"
            val tile = getOrNull(x + offX, y + offY, tiles)
            if (tile != null) {
                code = MapManager.shortTerrains[tile.priority]!!
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
            addEdgeSpritesForTile(tempTile, tempTile.shortCode, tempTile.tileType, tempTile.priority)
            usedTiles[keyCode] = tempTile
        }
        return usedTiles[keyCode]!!
    }

    fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {
        val tiles = Array(xBounds.count(), { x -> Array(yBounds.count(), { y -> generateTile(xBounds.elementAt(x), yBounds.elementAt(y)) }) })

        /*
        The extra sprite functionality must be adressed here, I guess? How do we manage
        the edges of the bigger map?

        If we can't find the neighbourtiles, we just skip it and flag the tile
        as needing neighbours - then we can check each tile when displaying and fix them then!

        Checking a true-false is fast, I imagine.
         */

        //This is like orto or something
        for ((x, column) in tiles.withIndex())
            for ((y, row) in column.withIndex()) {
                val tempTile = fixNeighbours(tiles[x][y], x, y, tiles)

                tiles[x][y] = tempTile
            }

        return Array(xBounds.count(),
            { column ->
                Array(yBounds.count(),
                    { row ->
                        tiles[column][row]!!
                            .getInstance(
                                xBounds.elementAt(column),
                                yBounds.elementAt(row))
                    })
            })
    }

    fun getDirectionFromIndex(index: Int): String {
        return when (index) {
            0 -> "north"
            1 -> "east"
            2 -> "south"
            3 -> "west"
            else -> "north"
        }
    }

    fun addEdgeSpritesForTile(ourTile: Tile, shortCode: String, tileType: String, priority: Int) {

        if (!MapManager.noExtraSprites.contains(shortCode) && !Assets.codeToExtraTiles.containsKey(shortCode)) {

            /*
            The code below is retarded. We should use the shortCode to resolve this, easy!
             */
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
                Assets.codeToExtraTiles[shortCode] = extraSprites.map { Assets.sprites[it.first]!![it.second]!! }
            else
                MapManager.noExtraSprites.add(shortCode)
        }
    }

    fun generateTile(x: Int, y: Int): Tile {
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