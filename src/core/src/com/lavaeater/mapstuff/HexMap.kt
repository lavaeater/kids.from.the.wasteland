package com.lavaeater.mapstuff

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import org.codetome.hexameter.core.api.*
import org.codetome.hexameter.core.api.contract.SatelliteData
import org.codetome.hexameter.core.api.defaults.DefaultSatelliteData


/**
 * Created by tommie on 2017-10-07.
 */
class HexMap {

//    val hexes: HexagonalGridBuilder
    val hexGrid: HexagonalGrid<TileInfo> = HexagonalGridBuilder<TileInfo>()
        .apply {
            gridHeight = 10
            gridWidth = 10
            setGridLayout(HexagonalGridLayout.RECTANGULAR)
            orientation = HexagonOrientation.POINTY_TOP
            radius = 15.0
        }.build()

    fun render(batch:SpriteBatch) {
        //Not entirely sure  we need the delta, but whatevs
        hexGrid.hexagons.forEach { it.draw(batch)}
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