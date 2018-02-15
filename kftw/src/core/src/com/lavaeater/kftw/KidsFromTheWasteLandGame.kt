package com.lavaeater.kftw

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets

class KidsFromTheWasteLandGame : ApplicationAdapter() {
    private lateinit var batch : SpriteBatch

    override fun create() {
        //You cannot init the spritebatch before the create method!
        batch = SpriteBatch()
        Assets.load()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        for(spriteCol in Assets.sprites.values)
            for(sprite in spriteCol.values)
                sprite.draw(batch)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}
