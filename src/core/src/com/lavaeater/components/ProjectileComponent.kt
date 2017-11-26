package com.lavaeater.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

/**
 * Created by tommie on 2017-06-29.
 */

class ProjectileComponent( val shooter: Entity, val linearSpeed:Float = 10f): Component