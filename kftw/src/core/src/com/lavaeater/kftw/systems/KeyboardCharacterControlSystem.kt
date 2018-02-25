package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.Body
import com.lavaeater.kftw.components.Box2dBodyComponent
import com.lavaeater.kftw.components.KeyboardControlComponent
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2
import java.util.*

class KeyboardCharacterControlSystem(val speed: Float = 20f):
    KtxInputAdapter,
    IteratingSystem(allOf(KeyboardControlComponent::class, Box2dBodyComponent::class).get(),45) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = kbCtrlMpr[entity]!!
        if(ctrlId != null || ctrlId != component.id) {
            ctrlId = component.id
            ctrlBody = b2bBMpr[entity]!!.body
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        ctrlBody?.linearVelocity = vec2(x, y).directionalVelocity(speed)
    }

    var y = 0f;
    var x = 0f
    val kbCtrlMpr = mapperFor<KeyboardControlComponent>()
    val b2bBMpr = mapperFor<Box2dBodyComponent>()

    var ctrlId : UUID? = null
    var ctrlBody : Body? = null

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> x = 1f
            Input.Keys.D -> x = -1f
            Input.Keys.W -> y = -1f
            Input.Keys.S -> y = 1f
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> x = 0f
            Input.Keys.D -> x = 0f
            Input.Keys.W -> y = 0f
            Input.Keys.S -> y = 0f
        }
        return true
    }
}