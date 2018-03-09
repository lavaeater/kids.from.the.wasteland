package com.lavaeater.kftw.data

import ktx.collections.gdxArrayOf
import com.badlogic.gdx.utils.Array as GdxArray

class Player(var name:String,
             var strength: Int = 10,
             var health: Int = 10,
             var intelligence: Int = 10,
             var sightRange: Int = 4,
             val skills: MutableMap<String, Int> = mutableMapOf("tracking" to 85)) {
  val inventory = gdxArrayOf("Mat", "Extra varm rock", "Litet, dåligt svärd")
}