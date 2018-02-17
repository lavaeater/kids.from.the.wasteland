package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.components.Transform
import com.lavaeater.kftw.components.WorldMapComponent
import com.lavaeater.kftw.systems.KeyboardCameraControlSystem
import com.lavaeater.kftw.systems.RenderMapSystem
import ktx.ashley.add
import ktx.ashley.entity

class WorldManager(val batch:SpriteBatch = SpriteBatch(),
                   val engine: Engine = Engine(),
                   val camera: OrthographicCamera = OrthographicCamera()):Disposable {

    val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)

    init {
        val inputSystem = KeyboardCameraControlSystem(camera)
        Gdx.input.inputProcessor = inputSystem
        engine.addSystem(inputSystem)
        engine.addSystem(RenderMapSystem(batch, camera))

        initMapEntity()
        camera.position.x = 300f
        camera.position.y = 300f
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
            with<Transform>()
        }
    }

    companion object {
        val VIEWPORT_HEIGHT = 640f
        val VIEWPORT_WIDTH = 480f
    }
}