package com.lavaeater.map

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

/**
 * The name parameter is the name of the sprite AND the physics body.
 */
class MapObject (val name: String, val scale: Float = 1f, val position: Vector2 = vec2(0f, 0f), val rotation: Float = 0f, val z: Int = 0, val isStartPoint:Boolean = false)