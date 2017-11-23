package com.lavaeater.mapstuff

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

open class Entity(var pos:Vector2, val texture: Texture) {
    var width:Float = 0f
    var height:Float = 0f

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, pos.x, pos.y, width, height)
    }
}

enum class TileType {
    Grass,
    Water,
    Cliff
}

class Tile(val x:Float, val y:Float, val size: Int, val type: TileType, texture: Texture): Entity(vec2(x,y), texture) {
    val isGrass = type == TileType.Grass
    val isWater = type == TileType.Water
    val isCliff = type == TileType.Cliff
}

