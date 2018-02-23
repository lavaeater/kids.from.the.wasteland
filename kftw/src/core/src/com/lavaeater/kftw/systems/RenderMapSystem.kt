package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.components.WorldMapComponent
import com.lavaeater.kftw.map.AreaMapManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.TileKey
import ktx.app.use
import ktx.ashley.allOf
import kotlin.math.roundToInt

class RenderMapSystem(val batch:SpriteBatch, val camera:OrthographicCamera, val mapManager: IMapManager) : IteratingSystem(allOf(WorldMapComponent::class).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        //This method will actually update the map? No?
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        batch.projectionMatrix = camera.combined
        batch.use {
            for (tileAndKey in mapManager.getVisibleTiles(camera.position)){
                val tile = tileAndKey.value
                val key = tileAndKey.key

                val sprite = Assets.sprites[tile.tileType]!![tile.subType]!!
                sprite.setPosition(key.x*8f, key.y*8f)
                sprite.draw(batch)

                //Fix this
                val west = tile.code[0]
                val east = tile.code[1]
                val north = tile.code[2]
                val south= tile.code[3]

                for(extra in tile.extraSprites) {
                    val extraSprite = Assets.sprites[extra.first]!![extra.second]!!
                    extraSprite.setPosition(key.x*8f, key.y*8f)
                    extraSprite.draw(batch)
                }
            }
        }
    }
}
//
//fun String.codeToExtraTiles() : Map<String, String> {
//    /*
//    The code is in chuncks, north to northwest, clockwise
//    for this purpose, we basically only care about... north, east, south, west, right?
//
//    The code is the type of tile in that direction
//
//    if north AND west are the grass, the map returned should be <"grass", "northwest">
//
//    Most efficient way to do this?
//     */
//    val shortCode = this.codeToShort()
//}

fun Array<Char>.isDirectionEqual(dir1:Int, dir2:Int) : Boolean {
    return this[dir1] == this[dir2]
}

fun String.codeToShortArr():Array<Char> {
    return arrayOf(this[0], this[2],this[4],this[6])
}

fun String.codeToShort():String {
    return "${this[0]}${this[2]}${this[4]}${this[6]}"
}

fun OrthographicCamera.toTile(factor: Int) : TileKey {
    return this.position.toTile(factor)
}

fun Vector3.toTile(factor: Int) : TileKey {
    return TileKey(this.tileX(factor), this.tileY(factor));
}

fun Vector2.toTile(factor: Int) : TileKey {
    return TileKey(this.tileX(factor), this.tileY(factor))
}

fun Vector2.tileX(factor:Int):Int {
    return (this.x / factor).roundToInt()
}
fun Vector2.tileY(factor:Int) : Int {
    return (this.y / factor).roundToInt()
}

fun Vector3.tileX(factor: Int) : Int {
    return (this.x / factor).roundToInt()
}

fun Vector3.tileY(factor:Int) : Int {
    return (this.y / factor).roundToInt()
}