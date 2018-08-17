package map

class UsedTiles {
    companion object {
        private val tiles = mutableMapOf<String, Tile>()
        fun tileExists(code: String) : Boolean {
            return tiles.containsKey(code)
        }

        fun put(code: String, tile: Tile) {
            tiles[code] = tile
        }

        fun get(code: String): Tile {
            return tiles[code]!!
        }

        fun tileFor(priority: Int, tileType: String, subType: String, code: String) :Tile {
            val tileCode = "$priority$tileType$subType$code$code${true}" //Only temporary, actually
            if(!tileExists(tileCode))
                put(tileCode, Tile(priority, tileType, subType, code, code))

            return get(tileCode)
        }
    }
}