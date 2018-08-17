package map

import Assets

/**
 * A tilemap is a class that keeps track of the tiles associated with a particular
 * location. It can load and save itself... no, that is managed by some persistence
 * service.
 *
 * As parameters it takes a chunksize, defining the size of the store chunks for this particular
 * map. There is really not that many reasons to change that value, though.
 *
 * A cool refactoring - a map should start with having 0,0 in the actual middle of that chunk, ie
 * on 31,31 - so that a smaller location only needs ONE chunk. This is probably trivial to fix.
 */
class TileMap(private val chunkSize: Int = 64,
              private val tileGenerator: TileGenerator) {

    private val upperBound = chunkSize - 1
    private val tileStores = mutableSetOf<TileStoreBase>()

    private fun lowerBoundFor(coordinate: Int): Int {
        if (coordinate < 0) {
            return ((coordinate + 1) / chunkSize) * chunkSize - chunkSize
        }
        return (coordinate / chunkSize) * chunkSize
    }

    private fun getTileStore(x: Int, y: Int): ITileStore {
        val lowerBoundX = lowerBoundFor(x)
        val lowerBoundY = lowerBoundFor(y)
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
		            tileGenerator.generateTilesForRange(
				            lX..(lX + upperBound),
				            lY..(lY + upperBound)))
            tileStores.add(store)
        }
        return store
    }

    fun getTile(x: Int, y: Int): TileInstance {
        val store = getTileStore(x, y)
        return store.getTile(x, y)
    }

    fun getTilesFlat(xBounds: IntRange, yBounds: IntRange) : Array<TileInstance> {

        lateinit var currentTile: TileInstance
        var currentStore = getTileStore(xBounds.start, yBounds.start)
        val columns = xBounds.count()
        val rows = yBounds.count()
        val size = columns * rows
        var row = 0
        var column = 0
        return Array(size) { _ ->

            val x = xBounds.start + column
            val y = yBounds.start + row

            if (x !in currentStore.xBounds || y !in currentStore.yBounds) {
                currentStore = getTileStore(x, y)
            }
            currentTile = currentStore.getTile(x, y)

            if (currentTile.tile.needsNeighbours) {
                fixNeighbours(currentTile.tile, x, y)
                    .updateInstance(currentTile)
            }

            column++
            if(column > xBounds.count() - 1) {
                column = 0
                row++
            }

            return@Array currentTile
        }
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


        MapService.neighbourMap.keys.forEach { (offX, offY) ->
            var code = "b"
            val t = getOrNull(x + offX, y + offY, tiles)
            if (t != null) {
                code = MapService.shortTerrains[t.priority]!!
            } else {
                needsNeighbours = true
            }
            tempTile.code += code
        }

        tempTile.needsNeighbours = needsNeighbours
        tempTile.shortCode = tempTile.code.toShortCode()

        val keyCode = tempTile.getKeyCode()

        if (!UsedTiles.tileExists(keyCode)) {
            //Add this new tile to the tile storage!
            addEdgeSpritesForTile(tempTile.shortCode, tempTile.priority)

	        UsedTiles.put(keyCode, tempTile)
        }
        return UsedTiles.get(keyCode)
    }

    private fun addEdgeSpritesForTile(shortCode: String, priority: Int) {

        if (!MapService.noExtraSprites.contains(shortCode) && !Assets.codeToExtraTiles.containsKey(shortCode)) {
            val tileC = shortCode.toCharArray()[0]
            val actualShortCode = shortCode.substring(1..4) //leave out the tile itself.
            val extraSprites = mutableListOf<Pair<String, String>>()
            val extraSpritesToRemove = mutableListOf<Pair<String, String>>()

            for ((index, code) in actualShortCode.withIndex()) {
                if (code != 'b' && tileC != code && MapService.shortTerrainPriority[code]!! > priority) {
                    if (extraSprites.any { it.first == MapService.shortLongTerrains[code]!! }) {
                        //Evaluate for diffs etc
                        for (extraSprite in extraSprites.filter { it.first == MapService.shortLongTerrains[code]!! }) {

                            /*
                      This type of tile exists, it might actually be relevant to
                      remove the existing one in favor of this one!
                       */
                            if (MapService.weirdDirections.containsKey("${getDirectionFromIndex(index)}${extraSprite.second}")) {
                                //Modify existing one, making it weird!
                                extraSpritesToRemove.add(extraSprite)
                                extraSprites.add(Pair(extraSprite.first, MapService.weirdDirections["${getDirectionFromIndex(index)}${extraSprite.second}"]!!))
                            } else {
                                extraSprites.add(Pair(MapService.shortLongTerrains[code]!!, getDirectionFromIndex(index)))
                            }
                        }
                    } else {
                        //just add
                        extraSprites.add(Pair(MapService.shortLongTerrains[code]!!, getDirectionFromIndex(index)))
                    }
                }
            }
            for (extraSprite in extraSpritesToRemove) {
                extraSprites.remove(extraSprite)
            }

            if (extraSprites.any())
                Assets.codeToExtraTiles[shortCode] = extraSprites.map { Assets.tileSprites[it.first]!![it.second]!! }
            else
                MapService.noExtraSprites.add(shortCode)
        }
    }


}