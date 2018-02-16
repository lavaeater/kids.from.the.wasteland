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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        /*
        So, this is gonna be ONE entity.

        It's a trick. We're gonna get the bounding rectangle and calculate what tiles we need.

        Then we're gonna draw those tiles. this is great.
         */
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.use {
            for (spriteCol in Assets.sprites.values)
                for (sprite in spriteCol.values)
                    sprite.draw(batch)
        }
    }
}