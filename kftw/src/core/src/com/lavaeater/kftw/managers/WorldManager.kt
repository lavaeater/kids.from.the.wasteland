package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.components.*
import com.lavaeater.kftw.systems.RenderMapSystem
import ktx.ashley.*


class WorldManager(val batch:SpriteBatch = SpriteBatch(),
                   val engine: PooledEngine = PooledEngine(),
                   val camera: OrthographicCamera = OrthographicCamera()):Disposable {

    val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)

    init {
        engine.addSystem(RenderMapSystem(batch))
        initMapEntity()
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
        val VIEWPORT_HEIGHT = 27f
        val VIEWPORT_WIDTH = 48f
    }
}