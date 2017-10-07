package com.lavaeater.mapstuff

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import org.codetome.hexameter.core.api.Hexagon
import org.codetome.hexameter.core.api.HexagonOrientation
import org.codetome.hexameter.core.api.HexagonalGridBuilder
import org.codetome.hexameter.core.api.HexagonalGridLayout
import org.codetome.hexameter.core.api.contract.SatelliteData


/**
 * Created by tommie on 2017-10-07.
 */
class Map {

//    val hexes: HexagonalGridBuilder
    val hexGrid = HexagonalGridBuilder<TileInfo>()
        .apply {
            gridHeight = 10
            gridWidth = 10
            setGridLayout(HexagonalGridLayout.RECTANGULAR)
            orientation = HexagonOrientation.POINTY_TOP
            radius = 20.0
        }.build()

    fun render(batch:SpriteBatch, delta:Float) {
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

class TileInfo(val spriteName: String) : SatelliteData {
    override fun setOpaque(opaque: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPassable(passable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPassable(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isOpaque(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMovementCost(movementCost: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMovementCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

infix fun Int.with(x:Int) = this.or(x)