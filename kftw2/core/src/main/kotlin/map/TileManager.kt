package map

import Assets
import com.badlogic.gdx.math.MathUtils
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
class Location(val name:String) {
    /*
    A location might be the world map
    It could be a town or a dungeon as well.

    The internal storage model of each and every one of these must be hidden
    from the render engine, the world engine, etc.

    So, basically we have map render system. It renders the map.

    Then we have an entity render system - but it renders all the entities in the world,
    how do we, easily, keep track of all entities that are valid for this particular world
    right now?

    - They have to be stored in the location. A location at least needs
    a list of IAgents to keep track of - and perhaps their Entities to be
    able to switch between them easily.

    A location also keeps track of its maxbounds and keeps everything regarding
    how it implements things like that under wraps...

    I see, in the near future, that we start using coroutines to render maps etc.

    So, what we need RIGHT NOW is a system for the user to bump into a dungeon. The dungeon will
    be placed on a rock tile bordering a forest. Easy.

    The user must be presented a choice to enter the dungeon.

    The dungeon must then be generated and the LOCATION needs to be changed.

    So all locations work the exact same. They represent the "context" for the world, so to
    speak. So, we can surely create a fact that represents this!



     */
}

class TileManager(private val chunkSize:Int = 100) {
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
        return Array(size, { _ ->

            val x = xBounds.start + column
            val y = yBounds.start + row

            if (x !in currentStore.xBounds || y !in currentStore.yBounds) {
                currentStore = getTileStore(x, y)
            }
            currentTile = currentStore.getTile(x, y)

            if (currentTile.tile.needsNeighbours) {
                fixNeighbours(currentTile.tile, x, y).updateInstance(currentTile)
//                putTile(x, y, currentTile)
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

            /*
            After this, we should definitely combine the sprites into one
            new texture. That would be supernice
             */
//            val mainSprite = tempTile.getSprite()
//            val pixMap = Pixmap(mainSprite.width.toInt(), mainSprite.height.toInt(), Pixmap.Format.RGBA8888)
//
//            val m = mainSprite.texture.textureData.consumePixmap()
//
//            m.pixels


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

        val tiles = Array(
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

        /*
        Try making a little thread?
         */

        Thread(Runnable {
            /*
            This would be very cool
             */


            /**
             * Now for some action!
             *
             * 1. place rooms
             *
             * rooms need AT LEAST
             *
             * three tiles of rock between them. This is great!
             */
            val rooms = mutableListOf<Room>()
            for(roomIndex in 0..MathUtils.random(25, 50)) {
                val width = MathUtils.random(5, 15)
                val height = MathUtils.random(5, 15)
                /*
								randomly place it for n tries.
								after n tries, this room fails and we continue
								with a new one.
								 */
                var tries = 0
                var failed = true
                while (tries < 10 && failed) {
                    val topLeftX = MathUtils.random(0, xBounds.count() - width - 4)
                    val topLeftY = MathUtils.random(0, yBounds.count() - height - 4)

                    val leftX =MathUtils.clamp(topLeftX - 3, 0, topLeftX - 3)
                    val leftY = MathUtils.clamp(topLeftY - 3, 0, topLeftY - 3)

                    var allRock = true
                    for (x in leftX..leftX + width + 6)
                        for (y in leftY..leftY + height + 6) {
                            val tileInstance = bigempty[x][y]
                            if(tileInstance.tile.priority != 3)
                                allRock = false
                        }
                    if(allRock) {
                        failed = false
                        val room = Room(topLeftX, topLeftY, width, height)
                        rooms.add(room)
                        for (x in topLeftX..topLeftX + width)
                            for (y in topLeftY..topLeftY + height) {
                                Thread.sleep(1)
                                val priority = 1//desert
                                val tileType = MapManager.terrains[priority]!!
                                val code = MapManager.shortTerrains[priority]!!
                                val subType = "center${MathUtils.random.nextInt(3) + 1}"
                                val tile = tileFor(priority, tileType, subType, code)
                                //What happens to the old one? eh?
                                tile.updateInstance(bigempty[x][y])
                                room.tileInstances.add(bigempty[x][y])
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
            val desertPriority = MapManager.terrainPriorities["desert"]!!
            val grassPriority = MapManager.terrainPriorities["grass"]!!
            val rockPriority = MapManager.terrainPriorities["rock"]!!
            val grassTile = tileFor(desertPriority, MapManager.terrains[grassPriority]!!, "center1", MapManager.shortTerrains[grassPriority]!!)
            val rockTile = tileFor(rockPriority, MapManager.terrains[rockPriority]!!, "center2", MapManager.shortTerrains[rockPriority]!!)
            val tunnels = mutableListOf<MutableList<TileInstance>>()

            while(tilesLeftToCheck) {
                val startTile = allTheRocks.firstOrNull()
                if(startTile == null) {
                    tilesLeftToCheck = false
                    continue
                }
                startTile.blinking = true
                allTheRocks.remove(startTile)
                if(startTile.noNeighboursAre("desert", tilesByKey)) {
                    val currentRoute = mutableListOf<TileInstance>()

                    /*
										Start of a route. Now, I want to keep the routes lying around for some tests
										and tweaks (and deletions) later.
										 */

                    /*
								We have a start tile. Choose a random direction and make a maze if that direction is valid.

								What is a valid direction or tile? Well, a valid direction or tile is obviously a tile that is "oneterrain"

								So we just get all the neighbours of a tile and filter out the ones that are all rock. If there is none that actually
								is all rock, then this maze is done!
								 */
                    var deadEndNotFound = true
                    var currentTile = startTile!!
                    var currentDirection = ""
                    var candidates: Map<String, TileInstance?>

                    /*
                    We should only turn left or right, not randomly select north, east, west, south
                    This means that when we try, we figure out what "left" means for "north" - it means west. For west it's south...
                    and so on. This means that from the start tile we can go any of the four directions
                    but from any subsequent tile, we can only go forward, left or right, it's the only relevant choices.
                    So after the first the available directions should always be forward, left and right for the current direction.
                     */

                    while(deadEndNotFound) {
                        //The current tile must be made into desert:

                        /*
                        After that, check what directions are valid for this tile. A direction is valid if it is rock
                        all around except for the tile
                        BEHIND it. There must be a neat algorithm for this...

                        The first run, there is no "current direction", we need to check ALL directions!
                        */
                        if(currentDirection == "") {
                            candidates = MapManager.simpleDirections.map {
                                Pair(it.value, Pair(
                                    it.key.first + currentTile.x,
                                    it.key.second + currentTile.y)) }
                                .map { Pair(it.first, tilesByKey[it.second]) }.toMap()
                        } else {
                            candidates =  MapManager.forwardLeftRight[currentDirection]!!.map {
                                Pair(it, Pair(
                                    MapManager.simpleDirectionsInverse[it]!!.first + currentTile.x,
                                    MapManager.simpleDirectionsInverse[it]!!.second + currentTile.y)) }
                                .map { Pair(it.first, tilesByKey[it.second]) }.toMap()
                        }

                        /*
                        How do we know if a candidate direction is "valid"?
                        Well, it is valid if IT and both the tiles to the left and right are
                        rock
                         */

                        val validCandidates = candidates.filter {
                            it.value != null &&
                                it.value!!.tile.tileType == "rock" &&
                                it.value!!.leftRightAndForwardAreNot(it.key, setOf("desert", "grass"), tilesByKey)
                        }.map { it.key to it.value!! }.toMap().toMutableMap()

                        if(validCandidates.any()) {
                            Thread.sleep(3)

                            for (tile in validCandidates)
                                tile.value.blinking = true

                            grassTile.updateInstance(currentTile)
                            currentRoute.add(currentTile)

                            if(currentDirection == "" || !validCandidates.containsKey(currentDirection)) {
                                currentDirection = validCandidates.keys.elementAt(MathUtils.random(0, validCandidates.size - 1))
                            }

                            if(MathUtils.random(1, 100) < 35) {
                                currentDirection = validCandidates.keys.elementAt(MathUtils.random(0, validCandidates.keys.size - 1))
                            }
                            currentTile = validCandidates[currentDirection]!!

                            for(c in validCandidates.filter { it.key != currentDirection }.values) {
                                Thread.sleep(3)
                                c.blinking = false
                            }

                            allTheRocks.remove(currentTile)
                            continue
                        }
                        deadEndNotFound = false
                    }
                    if(currentRoute.count() > 3) {
                        tunnels.add(currentRoute)
                      currentRoute.blink(true)
                    } else {
                        currentRoute.forEach{
                            Thread.sleep(3)
                          currentRoute.blink(false)
                            rockTile.updateInstance(it)
                        }
                      currentRoute.clear()
                    }
                }
              startTile.blinking = false
            }

            /*
             * 3. connect rooms
             * how?
             *
             * Connector tiles are tiles that:
              *
              * is made of rock
              * neighbour rooms
              * neighbour a route
             *
             * do a room at a time
             */

            for(room in rooms) {
                //check all surrounding tiles,
                //except for corners!
                val conns = mutableListOf<TileInstance>()
                val topX = room.topLeftX - 1
                val topY = room.topLeftY - 1 //y is up, right?
                val width = room.width + 2
                val height = room.height + 2
                val bottomY = topY + height
                val farX = topX + width

                room.toggleBlink()

                for (x in room.topLeftX..room.topLeftX + room.width) {
                    //y = topY!
                    if (x > 0 && x < bigempty.size - 1 &&
                        topY > 0 && topY < bigempty[x].size - 1 &&
                        bigempty[x][topY].neighbourToIs("north", "grass", tilesByKey))
                        conns.add(bigempty[x][topY])

                    if (x > 0 && x < bigempty.size - 1 &&
                        bottomY > 0 && bottomY < bigempty[x].size - 1 &&
                        bigempty[x][bottomY].neighbourToIs("south", "grass", tilesByKey)) {
                        conns.add(bigempty[x][bottomY])
                    }
                    conns.blink(true)
                }

                //bottomrow
                for (y in room.topLeftY..room.topLeftY + room.height) {
                    if (topX > 0 && topX < bigempty.size - 1 &&
                        y > 0 && y < bigempty[topX].size - 1 &&
                        bigempty[topX][y].neighbourToIs("west", "grass", tilesByKey))
                        conns.add(bigempty[topX][y])

                    if (topX > 0 && topX < bigempty.size - 1 &&
                        y > 0 && y < bigempty[topX].size - 1 &&
                        bigempty[farX][y].neighbourToIs("east", "grass", tilesByKey)) {
                        conns.add(bigempty[farX][y])
                    }
                    conns.blink(true)
                }

                if (conns.any()) {
                    val selectedTile = conns.elementAt(MathUtils.random(0, conns.size - 1))
                    conns.remove(selectedTile)
                    grassTile.updateInstance(selectedTile)
                    for (t in conns) {
                        rockTile.updateInstance(t)
                    }
                    conns.filter{ it != selectedTile }.blink(false)
                    conns.clear()
                }

                room.toggleBlink()
            }

//            for(list in tunnels) {
//                for(tile in list) {
//                    Thread.sleep(1)
//                    desertTile.updateInstance(tile)
//                }
//            }
        }).start()

        //Map big empty to tiles!

        /**
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

        val bigempty =
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

data class Room(
    val topLeftX:Int,
    val topLeftY:Int,
    val width: Int,
    val height:Int,
    val tileInstances: MutableList<TileInstance> = mutableListOf())
