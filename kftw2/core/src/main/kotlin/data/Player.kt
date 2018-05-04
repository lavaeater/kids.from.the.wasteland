package com.lavaeater.kftw.data

import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.map.TileKey
import ktx.collections.toGdxArray
import map.TileKeyManager
import com.badlogic.gdx.utils.Array as GdxArray

class Player(override var name:String,
             override var strength: Int = 10,
             override var health: Int = 10,
             override var intelligence: Int = 10,
             override var sightRange: Int = 3,
             override var currentTile: TileKey = Ctx.context.inject<TileKeyManager>().tileKey(0,0)) : IAgent {
  override val inventory = mutableListOf("Mat", "Extra varm rock", "Litet, dåligt svärd")
  val gdxInventory = inventory.toGdxArray()
  override val skills: MutableMap<String, Int> =
      mutableMapOf(
          "tracking" to 50,
          "stealth" to 35)
}

//data class Skill(val name: String)
//data class