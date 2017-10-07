package com.lavaeater

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.managers.WorldManager
import com.lavaeater.mapstuff.HexMap
import com.lavaeater.mapstuff.TileInfo
import org.codetome.hexameter.core.api.Hexagon

class WastelandKids : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    val worldManager = WorldManager()
    val map = HexMap()

    override fun create() {
       batch = SpriteBatch()
        Assets.load()
        //set some satellite data
        map.hexGrid.gridData
        map.hexGrid.hexagons.forEach {
            initHex(it)
        }
    }

    private fun initHex(hexagon: Hexagon<TileInfo>) {
        hexagon.setSatelliteData(TileInfo("grass_base"))
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        map.render(batch)
//        batch.draw(img, 0f, 0f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}
