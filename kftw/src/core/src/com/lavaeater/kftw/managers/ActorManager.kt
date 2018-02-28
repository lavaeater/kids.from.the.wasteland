package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.lavaeater.kftw.components.*
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.TileKey
import com.lavaeater.kftw.map.tileWorldCenter
import com.lavaeater.kftw.screens.Ctx
import ktx.math.vec2

class ActorManager {
  val engine = Ctx.context.inject<Engine>()
  val mapManager = Ctx.context.inject<IMapManager>()
  val bodyManager = Ctx.context.inject<BodyManager>()

  val npcTypes = mapOf(
      "townsfolk" to NpcType(4, 8, 2, 1, "lunges"))
  val npcNames = mapOf(1 to "Brage",
      2 to "Bork",
      3 to "Rygar",
      4 to "Bror",
      5 to "Fjalar",
      6 to "Yngve",
      7 to "Huggvold",
      8 to "Drago",
      9 to "Marjasin",
      10 to "Kingdok",
      11 to "Ronja",
      12 to "Signe",
      13 to "Ylwa",
      14 to "Skölda",
      15 to "Tagg",
      16 to "Farmor Ben",
      17 to "Hypatia",
      18 to "Wanja",
      19 to "Erika",
      20 to "Olga")

  fun addTownsFolk() {
    val potentialStartTiles = mapManager.getTilesInRange(TileKey(0, 0), 25)
        .filter { it.value.tileType == "grass" || it.value.tileType == "desert" }
        .map { it.key.tileWorldCenter(GameManager.TILE_SIZE) }
        .toTypedArray()

    for (i in 1..20)
      addNpcEntity(npcNames[i]!!, "townsfolk", potentialStartTiles)
  }

  fun addNpcEntity(name: String, type: String, startTiles : Array<Vector2>): Entity {

    val startPosition = startTiles[MathUtils.random(0, startTiles.size - 1)]

    val npc = Npc(name, npcTypes[type]!!)
    val reader = Gdx.files.internal("btrees/townfolk.tree").reader()
    val parser = BehaviorTreeParser<Npc>(BehaviorTreeParser.DEBUG_HIGH)
    val tree = parser.parse(reader, npc)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(tree))
      add(NpcComponent(npc))
      add(CharacterSpriteComponent(type))
      add(Box2dBodyComponent(createNpcBody(startPosition, npc)))
    }
    engine.addEntity(entity)
    return entity
  }

  fun addHeroEntity() : Entity {

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(CharacterSpriteComponent("femalerogue", true))
      add(KeyboardControlComponent())
      add(Box2dBodyComponent(bodyManager.createBody(2f, 2.5f, 15f, vec2(0f, 0f), BodyDef.BodyType.DynamicBody)))
    }
    engine.addEntity(entity)
    return entity
  }

  fun createNpcBody(position: Vector2, npc: Npc) : Body {
    return bodyManager.createBody(2f, 2.5f, 15f, position, BodyDef.BodyType.DynamicBody)
        .apply { userData = npc }
  }
}