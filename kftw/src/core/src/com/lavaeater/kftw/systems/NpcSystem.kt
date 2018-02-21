package com.lavaeater.kftw.systems

import com.badlogic.gdx.ai
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.math.Vector2
import com.lavaeater.kftw.components.NpcComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class NpcSystem : IteratingSystem(allOf(NpcComponent::class).get()) {

    /*
    Lets make some decisions!

    1. The townsperson is looking for some specific kind of tile
    To do this he checks the tile he's on and if not the correct type, he will try to find one

    He can look a certain distance of tiles around him (say two). If anyone in that range is of the correct type,
    he will walk there

    2. The townsperson is on the correct tile

    If the townsperson actually is on the correct tile, he will "do something" there, for x amount of time.

    This is great!
     */

    val mapper = mapperFor<NpcComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        var npcComponent = mapper[entity]

    }
}



class AiDef {
    val def = "root" +
            "   selector" +
            "       "
}