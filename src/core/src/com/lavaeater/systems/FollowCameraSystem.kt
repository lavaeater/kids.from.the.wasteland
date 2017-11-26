package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.lavaeater.components.TransformComponent
import ktx.ashley.mapperFor

class FollowCameraSystem(val camera: OrthographicCamera, trackedEntity:Entity): EntitySystem() {
    val transComponent = mapperFor<TransformComponent>().get(trackedEntity)

    override fun update(deltaTime: Float) {
        camera.position.set(transComponent.position.x,transComponent.position.y, 0f)
    }
}