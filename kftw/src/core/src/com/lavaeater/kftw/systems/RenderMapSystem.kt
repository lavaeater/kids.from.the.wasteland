package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.components.WorldMapComponent
import ktx.app.use
import ktx.ashley.allOf
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RenderMapSystem(val batch:SpriteBatch, val camera:OrthographicCamera) : IteratingSystem(allOf(WorldMapComponent::class).get()) {

    //val currentTileVector  get() = camera.toTile(8)
    val mapStructure = mutableMapOf<Pair<Int, Int>, String>()
//    val currentTileString get() = mapStructure[currentTileVector]
    val textureName = "grass"

    init {
        for (x in -100..100)
            for(y in -100..100) {
                val key = Pair(x, y)
                val index = MathUtils.random.nextInt(3) + 1
                val tileString = "center$index"
                mapStructure[key] = tileString
            }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        //This method will actually update the map? No?
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        batch.projectionMatrix = camera.combined
        batch.use {
            for (tile in mapStructure){
                val sprite = Assets.sprites[textureName]!![tile.value]!!
                sprite.setPosition(tile.key.first*8f, tile.key.second*8f)
                sprite.draw(batch)
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