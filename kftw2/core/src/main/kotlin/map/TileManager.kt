package map

import Assets
import com.badlogic.gdx.math.MathUtils
import com.sun.org.apache.xpath.internal.operations.Bool

/**
 * The tile manager class could be one
 * entry point for the concept of multiple maps.
 *
 * The TileManager actually delivers the tiles that we are using right now
 *
 * The Map Manager basically just delivers tiles for the rendering engine
 * and keeps track of very little, it's mostly pass-through to this
 * class.
 *
 * This class creates maps as we go, in the case of the world map.
 *
 * How do we partition this into something that can generate maps in
 * a different way?
 *
 * We need to partition up the code into the parts we need.
 *
 * We need some way to store and retrieve tiles. That's the tilestore class, which
 * can be reused for any type of map representation (but how do we handle "blank" tiles?
 * do we need a black sprite, or just mark it in some special way? Best would be to not draw
 * anything at all - because the background is black... OK; more on that later).
 *
 * The generation algorithm must be replaceable.
 *
 * We want to be able to use one algorithm for the world map (the current).
 * Then we want to be able to switch areas - this can be handled using a dictionary of
 * tilestores - one entry per "context" or id, if we need to save them later.
 *
 * So we have mutableMapOf("worldmap" to mutableSetOf<TileStoreBase>,
 * "dungeon_1" to mutableSetOf<TileStoreBase>
 *   and so on.
 */
class TileManager(val chunkSize:Int = 100) {
    private val upperBound = chunkSize - 1

    private val locations = mutableMapOf<String, MutableSet<TileStoreBase>>()
    private val locationAlgorithms = mutableMapOf<String, (IntRange, IntRange) -> Array<Array<TileInstance>>>()

    private var currentLocation = "worldmap"
    private var currentTileStores : MutableSet<TileStoreBase>
    private val usedTiles = mutableMapOf<String, Tile>() //can be used globally
    private var currentAlgorithm : (xBounds: IntRange, yBounds: IntRange)-> Array<Array<TileInstance>>

    init {
        currentLocation = "worldmap"
        currentTileStores = mutableSetOf()
        locations[currentLocation] = currentTileStores
        locationAlgorithms[currentLocation] = ::generateDungeonForRange
        currentAlgorithm = locationAlgorithms[currentLocation]!!
    }

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
        var store = currentTileStores.firstOrNull {
            lX in it.xBounds &&
                lY in it.yBounds
        }
        if (store == null) {
            store = FlatTileStore(
                lX,
                chunkSize,
                lY,
                chunkSize,
                currentAlgorithm(lX..(lX + upperBound), lY..(lY + upperBound)))
            currentTileStores.add(store)
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

    private fun generateDungeonForRange(xBounds: IntRange, yBounds: IntRange):Array<Array<TileInstance>> {
        /*

        Can we visualize this "live"

        Like, first generate the tiles as rock and then in a separate thread just keep modifying them?

        That would be really cool

        Dungeons, eh?

        Like, a dungeon or city is a world, in our weird abstraction, which is fine

        But how do we generate it?

        How do we create rooms, mazes and stuff like that?

        Ah. We create an array of tileinstances where all instances are the default, dark type. They
        could conceivably be the exact same instance, for sure...
         */

        val bigempty =
            Array(
                xBounds.count(),
                { x ->
                    Array(
                        yBounds.count(),
                        { y ->
                            tileFor(3,
                                MapManager.terrains[3]!!,
                            "center${MathUtils.random.nextInt(3) + 1}",
                                MapManager.shortTerrains[3]!!)
                                .getInstance(
                                    xBounds.elementAt(x),
                                    yBounds.elementAt(y))
                        })})


        //Map big empty to tiles!

        val tiles =             Array(
            xBounds.count(),
            { x ->
                Array(
                    yBounds.count(),
                    { y ->
                        bigempty[x][y].tile
                    })})

        for (column in bigempty.withIndex())
            for(row in column.value.withIndex()) {
                fixNeighbours(row.value.tile, column.index, row.index, tiles).updateInstance(row.value)
            }

        /**
         * Now for some action!
         *
         * 1. place rooms
         */
        val rooms = mutableListOf<Room>()
        for(roomIndex in 0..MathUtils.random(150, 200)) {
            val width = MathUtils.random(2, 20)
            val height = MathUtils.random(2, 20)
            /*
            randomly place it for n tries.
            after n tries, this room fails and we continue
            with a new one.
             */
            var tries = 0
            var failed = true
            while (tries < 10 && failed) {
                val topLeftX = MathUtils.random(0, xBounds.count() - width - 2)
                val topLeftY = MathUtils.random(0, yBounds.count() - height - 2)

                val leftX =MathUtils.clamp(topLeftX - 1, 0, topLeftX - 1)
                val leftY = MathUtils.clamp(topLeftY - 1, 0, topLeftY - 1)

                var allRock = true
                for (x in leftX..leftX + width + 2)
                    for (y in leftY..leftY + height +2) {
                        val tileInstance = bigempty[x][y]
                        if(tileInstance.tile.priority != 3)
                            allRock = false
                    }
                if(allRock) {
                    failed = false
                    rooms.add(Room(topLeftX, topLeftY, width, height))
                    for (x in topLeftX..topLeftX + width)
                        for (y in topLeftY..topLeftY + height) {
                            val priority = 1//desert
                            val tileType = MapManager.terrains[priority]!!
                            val code = MapManager.shortTerrains[priority]!!
                            val subType = "center${MathUtils.random.nextInt(3) + 1}"
                            val tile = tileFor(priority, tileType, subType, code)
                            //What happens to the old one? eh?
                            bigempty[x][y] = tile.getInstance(bigempty[x][y].x, bigempty[x][y].y)
                        }
                }

                tries++
            }
        }

        /**
         * 2. make mazes
         *
         * How are mazes made?
         *
         * a. find a tile made of rock where all neigbours are also rock.This is easy!
         */

        val flatTileCollectioN = bigempty.flatten()
        val allTheRocks = flatTileCollectioN.filter { it.tile.tileType == "rock" }.toMutableList()
        val tilesByKey = flatTileCollectioN.associateBy({Pair(it.x, it.y)}, {it})
        var tilesLeftToCheck = true
        val priority = MapManager.terrainPriorities["desert"]!!
        val desertTile = tileFor(priority, MapManager.terrains[priority]!!, "center1", MapManager.shortTerrains[priority]!!)

        while(tilesLeftToCheck) {
            val startTile = allTheRocks.firstOrNull()
            if(startTile == null) {
                tilesLeftToCheck = false
                continue
            }
            allTheRocks.remove(startTile)
            if(startTile.allNeighboursAre("rock", bigempty, xBounds.first, yBounds.first)) {

                /*
            We have a start tile. Choose a random direction and make a maze if that direction is valid.

            What is a valid direction or tile? Well, a valid direction or tile is obviously a tile that is "oneterrain"

            So we just get all the neighbours of a tile and filter out the ones that are all rock. If there is none that actually
            is all rock, then this maze is done!
             */
                var deadEndNotFound = true
                var currentTile = startTile!!
                val triedDirections = mutableListOf<String>()
                val availableDirections = MapManager.simpleDirectionsInverse.keys.toMutableList()
                var directionFound = false
                var currentDirection = ""
                var aCounter = 0

                while (deadEndNotFound) {

                    //the current tile needs to be changed into a desert tile! but we do grass instead
                    //because of how we do this, we should throw away the existing tile at some coordinate
                    //and replace it with a new one...
                    desertTile.updateInstance(currentTile) //We need to fix the goddamned short code!

                    if (!directionFound) {
                        currentDirection = availableDirections.elementAt(MathUtils.random(0, availableDirections.count() - 1))
                        availableDirections.remove(currentDirection)
                    }
                    directionFound = false
                    while (!directionFound && availableDirections.any()) {
                        aCounter++
                        /*
                    Sometimes, just randomly change direction for no good reason
                     */
                        if (MathUtils.random(1, 100) < 25 + aCounter) {
                            currentDirection = availableDirections.elementAt(MathUtils.random(0, availableDirections.count() - 1))
                        }

                        val nCoord = Pair(
                            currentTile.x + MapManager.simpleDirectionsInverse[currentDirection]!!.first,
                            currentTile.y + MapManager.simpleDirectionsInverse[currentDirection]!!.second)

                        val forwardCoord = Pair(
                            nCoord.first + MapManager.simpleDirectionsInverse[currentDirection]!!.first,
                            nCoord.second + MapManager.simpleDirectionsInverse[currentDirection]!!.second)

                        val candidate = tilesByKey[nCoord]
                        val forwardTile = tilesByKey[forwardCoord]
                        val fLeftCoord = Pair(
                            nCoord.first + MapManager.simpleLeft[currentDirection]!!.first,
                            nCoord.second + MapManager.simpleLeft[currentDirection]!!.second)
                        val fRightCoord = Pair(
                            nCoord.first + MapManager.simpleRight[currentDirection]!!.first,
                            nCoord.second + MapManager.simpleRight[currentDirection]!!.second)

                        val flTile = tilesByKey[fLeftCoord]
                        val frTile = tilesByKey[fRightCoord]

                        if (candidate != null && forwardTile != null && flTile != null && frTile != null) {
                            if (candidate.isOfType("rock") && forwardTile.isOfType("rock") && flTile.isOfType("rock") && frTile.isOfType("rock")) {
                                currentTile = candidate
                                allTheRocks.remove(currentTile)
                                allTheRocks.remove(forwardTile)
                                allTheRocks.remove(flTile)
                                allTheRocks.remove(frTile)
                                directionFound = true
                                continue
                            }
                        }
                        currentDirection = availableDirections.elementAt(MathUtils.random(0, availableDirections.count() - 1))
                        availableDirections.remove(currentDirection)
                    }

                    availableDirections.clear()
                    availableDirections.addAll(MapManager.simpleDirectionsInverse.keys)

                    deadEndNotFound = directionFound
                }
            }
        }



        /**
         * 3. connect rooms
         * 4. kill dead ends
         * 5. add encounters? how? Not here?
         */

        return bigempty
    }

    private fun generateCityForRange(xBounds: IntRange, yBounds: IntRange):Array<Array<TileInstance>> {
        /*
        Dungeons, eh?

        Like, a dungeon or city is a world, in our weird abstraction, which is fine

        But how do we generate it?

        How do we create rooms, mazes and stuff like that?

        Ah. We create an array of tileinstances where all instances are the default, dark type. They
        could conceivably be the exact same instance, for sure...
         */

        var bigempty =
            Array(
                xBounds.count(),
                { x ->
                    Array(
                        yBounds.count(),
                        { y ->
                            tileFor(1,
                                MapManager.terrains[1]!!,
                                "center${MathUtils.random.nextInt(3) + 1}",
                                MapManager.shortTerrains[1]!!)
                                .getInstance(
                                    xBounds.elementAt(x),
                                    yBounds.elementAt(y))
                        })})

        /**
         * Now for some action!
         *
         * 1. place rooms
         */
        for(roomIndex in 0..MathUtils.random(50, 100)) {
            val width = MathUtils.random(5, 20)
            val height = MathUtils.random(5, 20)
            /*
            randomly place it for n tries.
            after n tries, this room fails and we continue
            with a new one.
             */
            var tries = 0
            var failed = true
            while (tries < 10 && failed) {
                val topLeftX = MathUtils.random(0, xBounds.count() - width - 1)
                val topLeftY = MathUtils.random(0, yBounds.count() - height - 1)

                var allRock = true
                for (x in topLeftX..topLeftX + width)
                    for (y in topLeftY..topLeftY + height) {
                        val tileInstance = bigempty[x][y]
                        if(tileInstance.tile.priority != 1)
                            allRock = false
                    }
                if(allRock) {
                    failed = false

                    for (x in topLeftX..topLeftX + width)
                        for (y in topLeftY..topLeftY + height) {
                            val priority = 3//desert
                            val tileType = MapManager.terrains[priority]!!
                            val code = MapManager.shortTerrains[priority]!!
                            val subType = "center${MathUtils.random.nextInt(3) + 1}"
                            val tile = tileFor(priority, tileType, subType, code)
                            //What happens to the old one? eh?
                            bigempty[x][y] = tile.getInstance(bigempty[x][y].x, bigempty[x][y].y)
                        }
                }

                tries++
            }
        }
        //Try to place it by getting the tiles that are within the same bounds

//        for(x in xBounds.withIndex())
//            for(y in yBounds.withIndex()) {
//
//            }
        /**
         * 2. make mazes
         * 3. connect rooms
         * 4. kill dead ends
         * 5. add encounters? how? Not here?
         */

        return bigempty
    }

    private fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>> {
        val tiles = Array(
            xBounds.count(),
            { x ->
                Array(yBounds.count(),
                    { y ->
                        generateTile(
                            xBounds.elementAt(x),
                            yBounds.elementAt(y)) }) })

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

    private fun tileFor(priority: Int, tileType: String, subType: String, code: String) :Tile {
        val tileCode = "$priority$tileType$subType$code$code${true}" //Only temporary, actually
        if(!usedTiles.containsKey(tileCode))
            usedTiles[tileCode] = Tile(priority, tileType, subType, code, code)

        return usedTiles[tileCode]!!
    }

    private fun generateTile(x: Int, y: Int): Tile {
        val nX = x / MapManager.scale
        val nY = y / MapManager.scale

        val priority = getTilePriorityFromNoise(nX, nY, x, y)
        val tileType = MapManager.terrains[priority]!!
        val code = MapManager.shortTerrains[priority]!!
        val subType = "center${MathUtils.random.nextInt(3) + 1}"
        return tileFor(priority, tileType, subType, code)
    }
}

private fun TileInstance.allNeighboursAre(tileType: String, tiles: Array<Array<TileInstance>>, offsetX : Int, offsetY:Int) : Boolean {
    var allAreOfType = true
    for(coord in MapManager.neiborMap.keys) {
        val x = this.x + coord.first - offsetX
        val y = this.y + coord.second - offsetY
        if(x < tiles.size - 1 && x > 0 && y < tiles[x].size - 1 && y > 0) {
            allAreOfType = allAreOfType && tiles[x][y].tile.tileType == tileType
        } else {
            allAreOfType = false
        }
    }
    return allAreOfType
}

fun TileInstance.isOfType(terrain: String) :Boolean  {
    return this.tile.tileType.equals(terrain)
}

data class Room(
    val topLeftX:Int,
    val topLeftY:Int,
    val width: Int,
    val height:Int,
    val tileInstances: MutableList<TileInstance> = mutableListOf())