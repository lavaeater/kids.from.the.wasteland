package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.components.TransformComponent
import ktx.ashley.mapperFor

class FollowCameraSystem(val camera: OrthographicCamera, val trackedEntity : Entity) : EntitySystem(300){

    val transformComponet = mapperFor<TransformComponent>()[trackedEntity]
    val speed = 0.1f
    var y = 0f
    var x = 0f

    override fun update(deltaTime: Float) {
        camera.position.x = MathUtils.lerp(camera.position.x, transformComponet.x, speed)
        camera.position.y = MathUtils.lerp(camera.position.y, transformComponet.y, speed)
        camera.update(true)
    }
}