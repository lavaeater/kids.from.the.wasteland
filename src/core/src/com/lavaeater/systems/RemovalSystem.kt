package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.RemovalComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor


/**
 * Created by tommie on 2017-07-18.
 */
class RemovalSystem(val world:World):IteratingSystem(allOf(RemovalComponent::class, BodyComponent::class).get(), 999) {
    private val bcm = mapperFor<BodyComponent>()
//    private val removalQueue: Array<Entity>
//
//    init {
//         removalQueue = Array<Entity>()
//        }


    override fun processEntity(entity: Entity?, deltaTime: Float) {
        //get the body
        var body = bcm.get(entity!!)
        if(body != null)
        {
            body.body.userData = null
            world.destroyBody(body.body)
        }
        engine.removeEntity(entity)
    }
}