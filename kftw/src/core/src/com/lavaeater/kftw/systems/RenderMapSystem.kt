package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.components.WorldMapComponent
import com.lavaeater.kftw.managers.MapManager
import ktx.app.use
import ktx.ashley.allOf
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RenderMapSystem(val batch:SpriteBatch, val camera:OrthographicCamera) : IteratingSystem(allOf(WorldMapComponent::class).get()) {

    val mapManager = MapManager()
    override fun processEntity(entity: Entity?, deltaTime: Float) {

        //This method will actually update the map? No?
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        batch.projectionMatrix = camera.combined
        batch.use {
            for (tile in mapManager.mapStructure.values){

                val sprite = Assets.sprites[tile.tileType]!![tile.subType]!!
                sprite.setPosition(tile.key.first*8f, tile.key.second*8f)
                sprite.draw(batch)

                for(extra in tile.extraSprites) {
                    val extraSprite = Assets.sprites[extra.key]!![extra.value]!!
                    extraSprite.setPosition(tile.key.first*8f, tile.key.second*8f)
                    extraSprite.draw(batch)
                }
            }
        }
    }
}

fun OrthographicCamera.toTile(factor: Int) : Pair<Int, Int> {
    return Pair(this.position.tileX(factor), this.position.tileY(factor))
}

fun Vector3.tileX(factor: Int) : Int {
    return (this.x / factor).roundToInt()
}

fun Vector3.tileY(factor:Int) : Int {
    return (this.y / factor).roundToInt()
}