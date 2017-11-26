package com.lavaeater.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Game
import com.lavaeater.components.BodyComponent
import com.lavaeater.components.GamePadControllerComponent
import com.lavaeater.components.StateComponent
import com.lavaeater.util.XBox360Pad
import ktx.ashley.allOf
import ktx.ashley.mapperFor

/**
 * Created by barry on 12/9/15 @ 11:47 PM.
 */
class GamepadInputSystem : IteratingSystem(allOf(StateComponent::class, BodyComponent::class, GamePadControllerComponent::class).get()), ControllerListener {

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        return false
    }

    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        if(checkProcessing()) {
            val ctrl = controller!!
            if (Game.instance.hasPlayer(ctrl)) {
                val ce = Game.instance.getPlayer(ctrl)
                val state = stateComponentMapper.get(ce.entity)
                if (state != null && buttonCode == XBox360Pad.BUTTON_A)
                    state.takeTheFootOffTheGas()
            }
        }
        return false

    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return false
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        return false
    }

    override fun disconnected(controller: Controller?) {

    }

    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return false
    }

    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        return false
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        if(checkProcessing()) {
            val ctrl = controller!!
            if (Game.instance.players.values.filter { it.controller == ctrl }.any()) {
                val ce = Game.instance.players.filter{it.value.controller == ctrl}.keys.first()
                val state = stateComponentMapper.get(ce.entity)
                if (state != null && buttonCode == XBox360Pad.BUTTON_A)
                    state.pedalToTheMetal()
            }
        }
        return false
    }

    override fun connected(controller: Controller?) {
    }

    private val controllerByPlayerComponent: ComponentMapper<GamePadControllerComponent> = mapperFor()
    private val stateComponentMapper: ComponentMapper<StateComponent> = mapperFor()
    private val bodyComponentMapper: ComponentMapper<BodyComponent> = mapperFor()

    private val torque = 50f

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val controller = controllerByPlayerComponent.get(entity!!).controller

        val sc = stateComponentMapper.get(entity)
        val bc = bodyComponentMapper.get(entity)
        val body = bc.body

        if (sc.isThrusting()) {
            val rotation = body.angle - MathUtils.PI * 0.5f
           val thrustForce = 200f
           val forceVector = Vector2(MathUtils.cos(rotation), MathUtils.sin(rotation)).nor().scl(thrustForce)

            body.applyForce(forceVector, body.position, true)

            if (body.linearVelocity.len2() > 40 * 40f)
                body.linearVelocity.setLength2(40f * 40f)
        }

        if (sc.isTurningLeft()) {
            body.applyTorque(torque * sc.rotationFactor, true)
        }

        if (sc.isTurningRight()) {
            body.applyTorque(torque * sc.rotationFactor, true)
        }

            if (controller.getAxis(XBox360Pad.AXIS_LEFT_X) > 0.2f ||
                    controller.getAxis(XBox360Pad.AXIS_LEFT_X) < -0.2f)
                body.applyTorque(torque * -controller.getAxis(XBox360Pad.AXIS_LEFT_X) * 15f, true)

            if (controller.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER) < -0.2f)
                sc.startFirin()
            else if (controller.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER) > -0.1f)
                sc.stopFirin()

        if(sc.isJesusSteering()) {
            body.angularVelocity = 0f
        }

        if(body.angularVelocity > 5f)
            body.angularVelocity = 5f

        if(body.angularVelocity < -5f)
            body.angularVelocity = -5f
    }
}