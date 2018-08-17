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
    }
}