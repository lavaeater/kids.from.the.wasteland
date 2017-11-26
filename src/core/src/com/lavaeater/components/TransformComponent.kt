package com.lavaeater.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Created by barry on 12/8/15 @ 9:53 PM.
 */
class TransformComponent(var position:Vector3 = Vector3(0f,0f,0f), var scale:Vector2 = Vector2(1.0f, 1.0f), var rotation:Float = 0.0f, var isHidden:Boolean = false) : Component
