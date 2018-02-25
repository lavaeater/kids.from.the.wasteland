package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.components.*
import com.lavaeater.kftw.map.AreaMapManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.TileKey
import com.lavaeater.kftw.map.tileWorldCenter
import com.lavaeater.kftw.systems.*
import ktx.ashley.entity
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.math.vec2

class Messages {
  companion object {
    val CollidedWithImpassibleTerrain = 1000
  }
}

class GameManager(val batch: SpriteBatch = SpriteBatch(),
                  val engine: Engine = Engine(),
                  val camera: OrthographicCamera = OrthographicCamera(),
                  val messageDispatcher: MessageDispatcher = MessageManager.getInstance()) : Disposable {

  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val npcTypes = mapOf("townsfolk" to NpcType(4, 8, 2, 1, "lunges"))
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

  init {
//    val inputSystem = KeyboardCameraControlSystem(camera)
//    Gdx.input.inputProcessor = inputSystem
    engine.addSystem(RenderMapSystem(batch, camera, MapManager))
    engine.addSystem(RenderCharactersSystem(batch, camera))
    engine.addSystem(AiSystem())
    val npcControlSystem = NpcControlSystem()
    messageDispatcher.addListener(npcControlSystem, Messages.CollidedWithImpassibleTerrain)

    world.setContactListener(CollisionMessageManager(messageDispatcher))

    engine.addSystem(npcControlSystem)
    engine.addSystem(PhysicsSystem(world))
    engine.addSystem(PhysicsDebugSystem(world, camera))

    engine.addSystem(FollowCameraSystem(camera, addHero()))
    val inputSystem = KeyboardCharacterControlSystem()
    Gdx.input.inputProcessor = inputSystem
    engine.addSystem(inputSystem)

    initMapEntity()

    camera.position.x = 0f
    camera.position.y = 0f

    val potentialStartTiles = MapManager.getTilesInRange(TileKey(0,0), 15)
        .filter { it.value.tileType == "grass" || it.value.tileType == "desert" }
        .map { it.key.tileWorldCenter(TILE_SIZE) }
        .toTypedArray()

    for (i in 1..20)
      createNpc(npcNames[i]!!, "townsfolk", potentialStartTiles)

  }

  private fun addHero() : Entity {

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(CharacterSpriteComponent("femaleranger"))
      add(KeyboardControlComponent())
      add(Box2dBodyComponent(createBody(2f, 2.5f, 15f, vec2(0f,0f), BodyDef.BodyType.DynamicBody)))
    }
    engine.addEntity(entity)
    return entity
  }

  fun update(delta: Float) {
    engine.update(delta)
    messageDispatcher.update();
  }

  fun resize(width: Int, height: Int) {
    viewPort.update(width, height)
    batch.projectionMatrix = camera.combined
  }

  override fun dispose() {
    batch.dispose()
  }

  fun initMapEntity(): Unit {
    engine.entity {
      with<WorldMapComponent>()
    }
  }

  fun createEntity(): Entity {
    return engine.entity {
      with<TransformComponent>()
    }
  }

  fun createNpc(name: String, type: String, startTiles : Array<Vector2>): Entity {

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

  companion object {
    val VIEWPORT_HEIGHT = 128f
    val VIEWPORT_WIDTH = 96f
    val TILE_SIZE = 8
    val world = createWorld()
    val MapManager: IMapManager = AreaMapManager(world)

    fun createNpcBody(position: Vector2, npc: Npc) : Body {
      return createBody(2f, 2.5f, 15f, position, BodyDef.BodyType.DynamicBody)
          .apply { userData = npc }
    }

    fun createBody(width: Float,
                   height: Float,
                   densityIn: Float,
                   position: Vector2,
                   bodyType: BodyDef.BodyType): Body {

      val body = world.body {
        this.position.set(position)
        angle = 0f
        fixedRotation = true
        type = bodyType
        box(width, height) {
          density = densityIn
        }
      }
      return body
    }
  }
}