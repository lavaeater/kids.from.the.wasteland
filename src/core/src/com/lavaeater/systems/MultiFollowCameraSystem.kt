package com.lavaeater.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.components.FollowCameraComponent
import com.lavaeater.components.TransformComponent
import com.lavaeater.managers.MainGameManager
import ktx.ashley.allOf
import ktx.ashley.mapperFor

/**
 * Created by tommie on 2017-06-23.
 */

class MultiFollowCameraSystem(val camera: OrthographicCamera): EntitySystem() {

    private val family = allOf(FollowCameraComponent::class, TransformComponent::class).get()
    private val tc = mapperFor<TransformComponent>()
    private val aspectRatioHeight = MainGameManager.VIEWPORT_HEIGHT / MainGameManager.VIEWPORT_WIDTH
    private val aspectRatioWidth = MainGameManager.VIEWPORT_WIDTH / MainGameManager.VIEWPORT_HEIGHT

    override fun update(deltaTime: Float) {
        val entities = engine.getEntitiesFor(family)
        if(entities.any()) {
            val positions = entities.map { entity -> tc.get(entity).position }

            val posXs = positions.map { vector3 -> vector3.x }
            val posYs = positions.map { vector3 -> vector3.y }

            var width = MathUtils.clamp(posXs.max()!! - posXs.min()!!, MainGameManager.VIEWPORT_WIDTH,
                    MainGameManager.MAX_VIEWPORT_WIDTH) + 50f
            //val height = width * aspectRatioHeight
            var height = MathUtils.clamp(posYs.max()!! - posYs.min()!!,
                    MainGameManager.VIEWPORT_HEIGHT,
                    MainGameManager.MAX_VIEWPORT_HEIGHT) + 50f

            if (width < height * aspectRatioWidth) {
                width = height * aspectRatioWidth
            } else if (height < width * aspectRatioHeight) {
                height = width * aspectRatioHeight
            }

            camera.viewportWidth = width
            camera.viewportHeight = height

            camera.position.set(posXs.average().toFloat(), posYs.average().toFloat(), 0f)
        }
    }
}


