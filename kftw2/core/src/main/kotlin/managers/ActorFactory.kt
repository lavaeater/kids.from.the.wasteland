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
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.NpcType
import com.lavaeater.kftw.data.Player
import map.IMapManager
import com.lavaeater.kftw.injection.Ctx
import map.tileWorldCenter
import ktx.math.vec2
import story.AgentFactsManager

class ActorFactory {
  val engine = Ctx.context.inject<Engine>()
  val mapManager = Ctx.context.inject<IMapManager>()
  val bodyManager = Ctx.context.inject<BodyFactory>()

  val npcTypes = mapOf(
      "townsfolk" to NpcType(4, 8, 2, 1,3, 3, "lunges"),
      "sneakypanther" to NpcType(6, 10, 4, 3, 2, 3, "leaps and bites", startingTileTypes =  setOf("grass")),
      "snake" to NpcType(2, 2, 5, 5, 1, 2, "bites with venom", startingTileTypes =  setOf("desert")),
      "orc" to NpcType(4, 6, 2, 4, 3,  6,"swings a club", startingTileTypes =  setOf("desert", "grass"), skills = mapOf("stealth" to 25, "tracking" to 85)))

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

    val tileTypes = npcTypes["townsfolk"]!!.startingTileTypes

    val startPositions = mapManager.getTilesInRange(0, 0, 25)
        .filter { tileTypes.contains(it.tile.tileType) }
        .map { Pair(it.x,it.y).tileWorldCenter() }
        .toTypedArray()

    for (i in 1..20)
      addNpcEntity(npcNames[i]!!, "townsfolk", startPositions)
  }

  fun randomNpcName() : String {

    return npcNames[MathUtils.random(1, npcNames.size)]!!
  }

  fun randomNpcType(): String {
    return npcTypes.keys.toTypedArray()[MathUtils.random(npcTypes.keys.size - 1)]
  }

  fun addNpcEntity(name: String = randomNpcName(), type: String = randomNpcType(), startTiles : Array<Vector2>): Entity {

    val startPosition = startTiles[MathUtils.random(0, startTiles.size - 1)]
    return addNpcEntityAt(name, type, startPosition)
  }

  fun addNpcEntityAt(name: String = randomNpcName(), type: String = randomNpcType(), position: Vector2): Entity {
    val npc = Npc(name, npcTypes[type]!!)
    val reader = Gdx.files.internal("btrees/townfolk.tree").reader()
    val parser = BehaviorTreeParser<Npc>(BehaviorTreeParser.DEBUG_NONE)
    val tree = parser.parse(reader, npc)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(tree))
      add(NpcComponent(npc))
      add(AgentComponent(npc))
      add(CharacterSpriteComponent("townsfolk"))
      add(Box2dBodyComponent(createNpcBody(position, npc)))
    }
    engine.addEntity(entity)
    AgentFactsManager.addAgent(npc)
    return entity

  }

  fun addNpcEntityAtTile(name: String = randomNpcName(), type: String = randomNpcType(), x:Int, y:Int): Entity {
    val startPosition = Pair(x,y).tileWorldCenter()
    return addNpcEntityAt(name, type, startPosition)
  }

  fun addNpcAtTileWithAnimation(name: String = randomNpcName(), type: String, spriteKey:String, x:Int, y:Int) : Entity {

    val position = Pair(x,y).tileWorldCenter()
    val npc = Npc(name, npcTypes[type]!!)
    val reader = if(type == "orc") Gdx.files.internal("btrees/orc.tree").reader() else Gdx.files.internal("btrees/townfolk.tree").reader()
    val parser = BehaviorTreeParser<Npc>(BehaviorTreeParser.DEBUG_NONE)
    val tree = parser.parse(reader, npc)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(tree))
      add(NpcComponent(npc))
      add(AgentComponent(npc))
      add(CharacterSpriteComponent("orc", true))
      add(Box2dBodyComponent(createNpcBody(position, npc)))
    }
    engine.addEntity(entity)
    AgentFactsManager.addAgent(npc)
    return entity
  }

  fun addHeroEntity() : Entity {

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(CharacterSpriteComponent("femalerogue", true))
      add(KeyboardControlComponent())
      add(PlayerComponent(Ctx.context.inject()))
      add(AgentComponent(Ctx.context.inject<Player>()))
      add(VisibleComponent())
      add(Box2dBodyComponent(createPlayerBody(vec2(0f,0f),Ctx.context.inject())))
    }
    engine.addEntity(entity)
    return entity
  }

  fun createPlayerBody(position: Vector2, player:Player) : Body {
    return bodyManager.createBody(2f, 4f, 15f, position, BodyDef.BodyType.DynamicBody).apply { userData = player }
  }

  fun createNpcBody(position: Vector2, npc: Npc) : Body {
    return bodyManager.createBody(2f, 2.5f, 15f, position, BodyDef.BodyType.DynamicBody)
        .apply { userData = npc }
  }
}