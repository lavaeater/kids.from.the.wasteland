package map

interface TileGenerator {
    fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>>
}

class PerlinNoiseTileGenerator(private val terrainOptions: TerrainOptions) : TileGenerator {

}

data class TerrainOptions(val terrainProbabilities: )
