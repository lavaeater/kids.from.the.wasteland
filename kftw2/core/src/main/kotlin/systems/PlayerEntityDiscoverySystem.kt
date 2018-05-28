package systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.components.NpcComponent
import com.lavaeater.kftw.components.PlayerComponent
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.components.VisibleComponent
import map.isInRange
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.ashley.remove
import systems.toTile

class PlayerEntityDiscoverySystem(val playerEntity: Entity) :
    IntervalIteratingSystem(allOf(TransformComponent::class, NpcComponent::class).get(),0.25f,1) {

  val transMpr = mapperFor<TransformComponent>()
  val npcMpr = mapperFor<NpcComponent>()
  val visibilityMapper = mapperFor<VisibleComponent>()

  val player = mapperFor<PlayerComponent>()[playerEntity]!!.player

  override fun processEntity(entity: Entity) {

    val playerPos = transMpr[playerEntity].position.toTile()
    val npcPos = transMpr[entity].position.toTile()

    if(npcPos.isInRange(playerPos, player.sightRange)) {
      if(!entity.has(visibilityMapper)) {
        val playerSkill = player.skills["tracking"]!!
        val npc = npcMpr[entity].npc
        val npcSkill = npc.skills["stealth"]!!

        //How do we do discovery roll? Player skill - enemy counter skill, if under => success
        if (skillRoll(playerSkill, npcSkill)) {
          //The player sees the npc, it should now be rendered!
          entity.add(VisibleComponent())
        }
      }
    } else {
      //Entities that aren't in visibility range are invisible to the player!
      if(entity.has(visibilityMapper)) {
        entity.remove<VisibleComponent>()
      }
    }

  }

  private fun skillRoll(baseSkill: Int, skillModifier: Int): Boolean {
    return MathUtils.random(99) + 1 < baseSkill - skillModifier
  }

}