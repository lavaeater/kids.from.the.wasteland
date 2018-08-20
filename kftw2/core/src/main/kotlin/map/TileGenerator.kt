package map

interface TileGenerator {
    fun generateTilesForRange(xBounds: IntRange, yBounds: IntRange): Array<Array<TileInstance>>
}

/*
if (noiseValue in -100..-65)
    priority = 0
  if (noiseValue in -64..25)
    priority = 1
  if (noiseValue in 26..55)
    priority = 2
  if (noiseValue in 56..99)
    priority = 3
 */
data class TerrainOptions(val terrainProbabilities: Map<IntRange, String> = mapOf(
    -100..-65 to "water",
    -64..25 to "desert",
    26..55 to "grass",
    56..99 to "rock"))
