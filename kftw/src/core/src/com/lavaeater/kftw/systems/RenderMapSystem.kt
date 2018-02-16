package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf

class WorldMapComponent : Component

class RenderMapSystem : IteratingSystem(allOf(WorldMapComponent::class).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        /*
        So, this is gonna be ONE entity.

        It's a trick. We're gonna get the bounding rectangle and calculate what tiles we need.

        Then we're gonna draw those tiles. this is grea.
         */
    }
}