package data

import com.badlogic.gdx.math.MathUtils

interface IWorldlyThing {
  val id: String
  var worldX: Float
  var worldY: Float
  var tileX: Int
  var tileY: Int
  val tileKey: Pair<Int,Int>
}

interface IPlace : IWorldlyThing {
  /*
  A place in the world, can be entrance to a LOCATION

  Can this be loot? Maybe? Add more shit later?
   */
  var name: String
}

interface IAgent: IWorldlyThing {
  var name: String
  var strength: Int
  var health: Int
  var intelligence: Int
  var sightRange: Int
  val inventory: MutableList<String>
  val skills: MutableMap<String, Int>
  var speed: Int
  var attack: Int
  var attackString: String
}

fun IAgent.rollAgainstAgent(antagonist: IAgent, skill:String) : Boolean {
  val resistance = if(antagonist.skills[SkillMap.resistingSkills[skill]!!] != null) antagonist.skills[SkillMap.resistingSkills[skill]!!]!! else 0
  val skillValue = this.skills[skill]!! - resistance

  return MathUtils.random(1,99) + 1 < skillValue
}