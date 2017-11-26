package com.lavaeater.map

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

/**
 * Created by tommie on 2017-09-30.
 */
class GameMap(val name: String) {
    //Stupid container for map data, for serialization purposes.
    val mapObjects: MutableList<MapObject> = mutableListOf()
    fun mapObject(name:String, scale:Float = 1f, position: Vector2 = vec2(0f,0f), rotation: Float = 0f, z: Int = 0, isStartPoint: Boolean = false) {
        mapObjects.add(MapObject(name, scale, position, rotation, z, isStartPoint))
    }
}

