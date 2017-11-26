package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.lavaeater.Assets
import com.lavaeater.components.HealthComponent
import com.lavaeater.components.RemovalComponent
import com.lavaeater.components.SpriteComponent
import com.lavaeater.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2

/**
 * Created by tommie on 2017-07-20.
 */

class HealthSystem(val explosionAt: (x:Float, y:Float) -> Unit): IteratingSystem(allOf(HealthComponent::class).get()) {
    val healthMapper = mapperFor<HealthComponent>()
    val spriteMapper = mapperFor<SpriteComponent>()
    val transformMapper = mapperFor<TransformComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val hc = healthMapper.get(entity)

        if(!hc.player.alive) {
            val sprite = Assets.sprites[spriteMapper.get(entity).name]!!
            val entityPos = transformMapper.get(entity).position
            val pos = vec2(entityPos.x + sprite.width / 2, entityPos.y + sprite.height / 2)

            explosionAt(pos.x, pos.y)
            entity.add(RemovalComponent())
        }
    }
}