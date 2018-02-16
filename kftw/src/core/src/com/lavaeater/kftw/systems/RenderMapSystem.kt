package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import com.lavaeater.kftw.components.WorldMapComponent
import ktx.app.use
import ktx.ashley.allOf


class RenderMapSystem(val batch:SpriteBatch) : IteratingSystem(allOf(WorldMapComponent::class).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {

        //This method will actually update the map? No?
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.use {
            Assets.sprites.values
                    .flatMap { it.values }
                    .forEach { it.draw(batch) }
        }
    }
}