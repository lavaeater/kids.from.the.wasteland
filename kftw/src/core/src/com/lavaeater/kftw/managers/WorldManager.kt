package com.lavaeater.kftw.managers

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform(var x:Float=0f, var y:Float = 0f, var rotation:Float = 0f):Component

class WorldManager(val engine: PooledEngine = PooledEngine()) {

    fun createEntity() : Entity {
        return engine.entity {
            with<Transform>()
        }
    }
}