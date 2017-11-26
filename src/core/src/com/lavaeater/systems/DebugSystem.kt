package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

/**
 * Created by 78899 on 2017-09-28.
 */


class DebugSystem: IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get()) {
    val bodyMapper = mapperFor<BodyComponent>()
    val transMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val bc = bodyMapper.get(entity)
        val tc = transMapper.get(entity)
        Gdx.app.log("EntityStuff", "bodyPositon: ${bc.body.position} | transposition: ${tc.position} ")
    }

}