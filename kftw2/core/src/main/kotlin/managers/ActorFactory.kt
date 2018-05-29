package managers

import Assets
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import components.*
import data.Npc
import data.NpcType
import data.Player
import ktx.math.vec2
import map.IMapManager
import map.tileWorldCenter
import world.FactsOfTheWorld

class ActorFactory(
    private val engine: Engine,
    private val mapManager: IMapManager,
    private val bodyManager: BodyFactory,
    private val player: Player,
    private val factsOfTheWorld: FactsOfTheWorld) {

  val npcTypes = mapOf(
      "townsfolk" to NpcType("townsfolk", 4, 8, 2, 1,3, 3, "lunges"),
      "sneakypanther" to NpcType("sneakypanther",6, 10, 4, 3, 2, 3, "leaps and bites", startingTileTypes =  setOf("grass")),
      "snake" to NpcType("snake",2, 2, 5, 5, 1, 2, "bites with venom", startingTileTypes =  setOf("desert")),
      "orc" to NpcType("orc",4, 6, 2, 4, 3,  6,"swings a club", startingTileTypes =  setOf("desert", "grass"), skills = mapOf("stealth" to 25, "tracking" to 85)))

  fun addTownsFolk() {

    val tileTypes = npcTypes["townsfolk"]!!.startingTileTypes

    val startPositions = mapManager.getTilesInRange(0, 0, 25)
        .filter { tileTypes.contains(it.tile.tileType) }
        .map { Pair(it.x,it.y).tileWorldCenter() }
        .toTypedArray()

    for (i in 1..20)
      addNpcEntity(factsOfTheWorld.npcNames[i]!!, "townsfolk", startPositions)
  }

  fun randomNpcName() : String {

    return factsOfTheWorld.npcNames[MathUtils.random(1, factsOfTheWorld.npcNames.size)]!!
  }

  fun randomNpcType(): String {
    return npcTypes.keys.toTypedArray()[MathUtils.random(npcTypes.keys.size - 1)]
  }

  fun addNpcEntity(name: String = randomNpcName(), type: String = randomNpcType(), startTiles : Array<Vector2>): Entity {

    val startPosition = startTiles[MathUtils.random(0, startTiles.size - 1)]
    return addNpcEntityAt(name, type, startPosition)
  }

  fun addNpcEntityAt(name: String = randomNpcName(), type: String = randomNpcType(), position: Vector2): Entity {
    val npc = Npc(getNpcId(name), name, npcTypes[type]!!)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(npc.getBehaviorTree()))
      add(NpcComponent(npc))
      add(AgentComponent(npc))
      add(CharacterSpriteComponent("townsfolk"))
      add(Box2dBodyComponent(createNpcBody(position, npc)))
    }
    engine.addEntity(entity)
    return entity

  }

  fun addNpcEntityAtTile(name: String = randomNpcName(), type: String = randomNpcType(), x:Int, y:Int): Entity {
    val startPosition = Pair(x,y).tileWorldCenter()
    return addNpcEntityAt(name, type, startPosition)
  }

  fun addNpcAtTileWithAnimation(name: String = randomNpcName(), type: String, spriteKey:String ="", x:Int, y:Int) : Entity {

    val position = Pair(x,y).tileWorldCenter()
    val npc = Npc(getNpcId(name), name, npcTypes[type]!!)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(npc.getBehaviorTree()))
      add(NpcComponent(npc))
      add(AgentComponent(npc))
      add(VisibleComponent())
      add(CharacterSpriteComponent(npc.name.replace(" ", "").toLowerCase(), true))
      add(Box2dBodyComponent(createNpcBody(position, npc)))
    }
    engine.addEntity(entity)
    return entity
  }

  fun addHeroEntity() : Entity {

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(CharacterSpriteComponent("williamhamparsomian", true))
      add(KeyboardControlComponent())
      add(PlayerComponent(player))
      add(AgentComponent(player))
      add(VisibleComponent())
      add(Box2dBodyComponent(createPlayerBody(vec2(0f, 0f), player)))
    }
    engine.addEntity(entity)
    return entity
  }

  fun createPlayerBody(position: Vector2, player: Player) : Body {
    return bodyManager.createBody(2f, 4f, 15f, position, BodyDef.BodyType.DynamicBody).apply { userData = player }
  }

  fun createNpcBody(position: Vector2, npc: Npc) : Body {
    return bodyManager.createBody(2f, 2.5f, 15f, position, BodyDef.BodyType.DynamicBody)
        .apply { userData = npc }
  }

  companion object {
    var npcIds: Int = 0
    fun getNextNpcId():Int {
      return npcIds++
    }

    fun getNpcId(name:String):String {
      return "${name}_${getNextNpcId()}"
    }
  }
}

fun Npc.getBehaviorTree() : BehaviorTree<Npc> {

  val reader = if(this.npcType.name == "orc") Assets.readerForTree("orc.tree") else Assets.readerForTree("townfolk.tree")
  val parser = BehaviorTreeParser<Npc>(BehaviorTreeParser.DEBUG_NONE)
  return parser.parse(reader, this)
}