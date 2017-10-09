package com.lavaeater.mapstuff

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import com.lavaeater.IRenderable
import org.codetome.hexameter.core.api.*
import org.codetome.hexameter.core.api.defaults.DefaultSatelliteData


/**
 * Created by tommie on 2017-10-07.
 */
class HexMap : IRenderable {

//    val hexes: HexagonalGridBuilder
    val hexGrid: HexagonalGrid<TileInfo> = HexagonalGridBuilder<TileInfo>()
        .apply {
            gridHeight = 11
            gridWidth = 11
            setGridLayout(HexagonalGridLayout.HEXAGONAL)
            orientation = HexagonOrientation.POINTY_TOP
            radius = 16.5
        }.build()

    override fun render(batch: Batch) {
        //Not entirely sure  we need the delta, but whatevs
        hexGrid.hexagons.forEach { it.draw(batch as SpriteBatch)}
    }
}

fun Hexagon<TileInfo>.draw(batch: SpriteBatch) {
    if(satelliteData.isPresent) {
        val data = satelliteData.get()
        val sprite = Assets.sprites[data.spriteName]!!
        sprite.setPosition(centerX.toFloat(), centerY.toFloat())

        sprite.draw(batch)
    }
}

class TileInfo(val spriteName: String) : DefaultSatelliteData()

infix fun Int.with(x:Int) = this.or(x)