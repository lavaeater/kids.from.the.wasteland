package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.components.*
import com.lavaeater.kftw.map.AreaMapManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.systems.*
import ktx.ashley.entity

class GameManager(val batch: SpriteBatch = SpriteBatch(),
                  val engine: Engine = Engine(),
                  val camera: OrthographicCamera = OrthographicCamera()) : Disposable {

  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val npcTypes = mapOf("townsfolk" to NpcType(4, 8, 2, 1, "lunges"))

  init {
//    val inputSystem = KeyboardCameraControlSystem(camera)
//    Gdx.input.inputProcessor = inputSystem
//    engine.addSystem(inputSystem)
    engine.addSystem(RenderMapSystem(batch, camera, MapManager))
    engine.addSystem(RenderCharactersSystem(batch, camera))
    engine.addSystem(AiSystem())
    engine.addSystem(NpcControlSystem())

    initMapEntity()

    camera.position.x = 0f
    camera.position.y = 0f
    engine.addSystem(FollowCameraSystem(camera, createNpc("townsfolk")))

  }

  fun update(delta: Float) {
    engine.update(delta)
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

  fun createNpc(type: String): Entity {

    val npc = Npc(npcTypes[type]!!)
    val reader = Gdx.files.internal("btrees/townfolk.tree").reader()
    val parser = BehaviorTreeParser<Npc>(BehaviorTreeParser.DEBUG_HIGH)
    val tree    = parser.parse(reader, npc)

    val entity = engine.createEntity().apply {
      add(TransformComponent())
      add(AiComponent(tree))
      add(NpcComponent(npc))
      add(CharacterSpriteComponent(type))
    }
    engine.addEntity(entity)
    return entity
  }

  companion object {
    val VIEWPORT_HEIGHT = 64f
    val VIEWPORT_WIDTH = 48f
    val TILE_SIZE = 8
    val MapManager: IMapManager = AreaMapManager()
  }
}