package com.lavaeater.kftw.data

data class NpcType(val strength:Int, val health: Int, val speed: Int, val attack: Int, val attackString: String, val spawnProbs: Set<String> = setOf("grass", "desert"))
