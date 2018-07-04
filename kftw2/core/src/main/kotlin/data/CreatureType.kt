package data

data class CreatureType(val name : String,
                        val strength:Int,
                        val health: Int,
                        val speed: Int,
                        val attack: Int,
                        val intelligence: Int,
                        val sightRange: Int,
                        val attackString: String,
                        val inventory: MutableList<String> = mutableListOf("A thing", "A majig"),
                        val startingTileTypes: Set<String> = setOf("grass", "desert"),
                        val skills: Map<String, Int> = mapOf("stealth" to 25))
