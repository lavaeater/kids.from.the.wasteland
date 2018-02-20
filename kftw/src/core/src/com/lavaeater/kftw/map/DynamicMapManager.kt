package com.lavaeater.kftw.map

//class DynamicMapManager : MapManagerBase() {

//    override fun getNeighbours(key: Pair<Int, Int>) : List<Tile> {
//        val mutableList = mutableListOf<Tile>()
//        for (x in -1..1)
//            (-1..1)
//                    .asSequence()
//                    .filter { x != 0 || it != 0 }
//                    .forEach {
//                        var tile = getTileForKey(Pair(key.first + x, key.second + it), false)
//                        if(tile != null) mutableList.add(tile)
//                    }
//        return mutableList
//    }
//
//    fun getTileForKey(key: Pair<Int,Int>, createIfNotExists : Boolean = true): Tile? {
//        if(mapStructure.containsKey(key)) return mapStructure[key]!!
//        if(createIfNotExists) {
//            mapStructure[key] = createTile(key)
//        }
//        return mapStructure[key]
//    }
//
//    override fun createTile(key: Pair<Int, Int>): Tile {
//        val randomInt = (getNoise(key.first, key.second, 1.0,0.5) * 100).roundToInt()
//        var priority = 0
//        if(randomInt in 0..10)
//            priority = 0
//        if(randomInt in 11..70)
//            priority = 1
//        if(randomInt in 71..85)
//            priority = 2
//        if(randomInt in 85..99)
//            priority = 3
//        val tileType = terrains[priority]!!
//        val subType = getSubType()
//        val tile = Tile(key, priority, tileType, subType)
//
//        return tile
//    }
//
//    override fun getVisibleTiles(position: Vector3) : List<Tile> {
//        if(doWeNeedNewVisibleTiles(position)) {
//            currentKey = position.toTile(GameManager.TILE_SIZE)
//            visibleTiles.clear()
//            val startX = currentKey.first - widthInTiles
//            val stopX = currentKey.first + widthInTiles
//            val startY = currentKey.second - heightInTiles
//            val stopY = currentKey.second + heightInTiles
//            for (x in startX..stopX)
//            for(y in startY..stopY) {
//                visibleTiles.add(getTileForKey(Pair(x, y))!!)
//            }
//        }
//        visibleTiles.filter { !it.extraSpritesInitialized }.forEach { setExtraSprites(it) }
//        return visibleTiles
//    }
//}