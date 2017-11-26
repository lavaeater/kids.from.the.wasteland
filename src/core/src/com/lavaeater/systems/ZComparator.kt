package com.lavaeater.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.lavaeater.components.TransformComponent
import java.util.*

/**
 * Created by barry on 12/8/15 @ 10:22 PM.
 */
class ZComparator : Comparator<Entity> {
    private val transformM: ComponentMapper<TransformComponent>

    init {
        transformM = ComponentMapper.getFor<TransformComponent>(TransformComponent::class.java)
    }

    override fun compare(entityA: Entity, entityB: Entity): Int {
        return Math.signum(transformM.get(entityB).position.z - transformM.get(entityA).position.z).toInt()
    }
}

