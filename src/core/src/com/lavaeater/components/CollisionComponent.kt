package com.lavaeater.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

/**
 * Created by tommie on 2017-10-02.
 */
class CollisionComponent(val body: Body, val entity: Entity) : Component