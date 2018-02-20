package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.components.*
import com.lavaeater.kftw.systems.KeyboardCameraControlSystem
import com.lavaeater.kftw.systems.RenderCharactersSystem
import com.lavaeater.kftw.systems.RenderMapSystem
import ktx.ashley.entity

class GameManager(val batch:SpriteBatch = SpriteBatch(),
                  val engine: Engine = Engine(),
                  val camera: OrthographicCamera = OrthographicCamera()):Disposable {

    val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
    val npcTypes = mapOf("townsfolk" to NpcType(4, 8, 2, 1, "lunges"))

    init {
        val inputSystem = KeyboardCameraControlSystem(camera)
        Gdx.input.inputProcessor = inputSystem
        engine.addSystem(inputSystem)
        engine.addSystem(RenderMapSystem(batch, camera))
        engine.addSystem(RenderCharactersSystem(batch, camera))

        initMapEntity()
        createNpc("townsfolk")
        camera.position.x = 0f
        camera.position.y = 0f
    }

    fun update(delta:Float) {
        engine.update(delta)
    }

    fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    override fun dispose() {
        batch.dispose()
    }

    fun initMapEntity() : Unit {
        engine.entity {
            with<WorldMapComponent>()
        }
    }

    fun createEntity() : Entity {
        return engine.entity {
            with<TransformComponent>()
        }
    }

    fun createNpc(type: String) : Unit {

        val entity = engine.createEntity()
        entity.add(TransformComponent())
        entity.add(NpcComponent(Npc(npcTypes[type]!!)))
        entity.add(CharacterSpriteComponent(type))
        engine.addEntity(entity)
    }

    companion object {
        val VIEWPORT_HEIGHT = 64f
        val VIEWPORT_WIDTH = 48f
        val TILE_SIZE = 8
    }
}