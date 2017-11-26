package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.lavaeater.components.ZPositionComponent
import ktx.ashley.mapperFor
import java.util.*

class ZPositionComparator : Comparator<Entity> {
    val zMapper = mapperFor<ZPositionComponent>()

    override fun compare(entityA: Entity, entityB: Entity): Int {
        return Math.signum(zMapper.get(entityB).z - zMapper.get(entityA).z).toInt()
    }
}