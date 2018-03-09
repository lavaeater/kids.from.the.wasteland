package com.lavaeater.kftw.data

import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.map.TileKey

interface IAgent {
  var name: String
  var strength: Int
  var health: Int
  var intelligence: Int
  var sightRange: Int
  val inventory: MutableList<String>
  val skills: MutableMap<String, Int>
  var currentTile: TileKey
}

fun IAgent.rollAgainstAgent(antagonist: IAgent, skill:String) : Boolean {
  val resistance = if(antagonist.skills[SkillMap.resistingSkills[skill]!!] != null) antagonist.skills[SkillMap.resistingSkills[skill]!!]!! else 0
  val skillValue = this.skills[skill]!! - resistance

  return MathUtils.random(1,99) + 1 < skillValue
}