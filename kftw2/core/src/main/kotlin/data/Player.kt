package com.lavaeater.kftw.data



class Player(override val id:String = "Player",
             override var name:String,
             override var strength: Int = 10,
             override var health: Int = 10,
             override var intelligence: Int = 10,
             override var sightRange: Int = 3,
             override var currentX:Int = 0,
             override var currentY:Int = 0) : IAgent {
  override val inventory = mutableListOf("Mat", "Extra varm rock", "Litet, dåligt svärd")
  override val skills: MutableMap<String, Int> =
      mutableMapOf(
          "tracking" to 50,
          "stealth" to 35)
}

