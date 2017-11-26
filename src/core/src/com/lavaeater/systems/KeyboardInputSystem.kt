package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.StateComponent
import ktx.ashley.mapperFor

class KeyboardInputSystem(val entity: Entity) : EntitySystem(), InputProcessor {
    val stateComponent = mapperFor<StateComponent>().get(entity)
    val body = mapperFor<BodyComponent>().get(entity).body
    val torque = 200f

    override fun update(deltaTime: Float) {
        if (stateComponent.isThrusting()) {
            val rotation = body.angle - MathUtils.PI * 0.5f
            val thrustForce = 200f
            val forceVector = Vector2(MathUtils.cos(rotation), MathUtils.sin(rotation)).nor().scl(thrustForce)

            body.applyForce(forceVector, body.position, true)

            if (body.linearVelocity.len2() > 40 * 40f)
                body.linearVelocity.setLength2(40f * 40f)
        }

        if (stateComponent.isTurningLeft()) {
            body.applyTorque(torque * stateComponent.rotationFactor, true)
        }

        if (stateComponent.isTurningRight()) {
            body.applyTorque(torque * stateComponent.rotationFactor, true)
        }

        if(stateComponent.isJesusSteering()) {
            body.angularVelocity = 0f
        }

        if(body.angularVelocity > 5f)
            body.angularVelocity = 5f

        if(body.angularVelocity < -5f)
            body.angularVelocity = -5f



    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {return true
    }

    override fun keyTyped(character: Char): Boolean {return true    }

    override fun scrolled(amount: Int): Boolean {
return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> stateComponent.jesusTakeTheWheel()
            Input.Keys.D -> stateComponent.jesusTakeTheWheel()
            Input.Keys.W -> stateComponent.takeTheFootOffTheGas()
            Input.Keys.SPACE -> stateComponent.stopFirin()
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> stateComponent.turnLeft()
            Input.Keys.D -> stateComponent.turnRight()
            Input.Keys.W -> stateComponent.pedalToTheMetal()
            Input.Keys.SPACE -> stateComponent.startFirin()
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }
}