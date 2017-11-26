package com.lavaeater.components

import com.badlogic.ashley.core.Component

/**
 * Created by barry on 12/8/15 @ 8:30 PM.
 */
class StateComponent : Component {
    private var thrustState = "DEFAULT"
    private var rotationState = "NONE"
    var time = 0.0f
    var lastShotDelta = 0f
    var isLooping = false
    var isHit: Boolean = false
    var isFiring: Boolean = false
    var rotationFactor = 0f

    fun pedalToTheMetal() {
        thrustState = "THRUSTING"
        time = 0.0f
    }

    fun takeTheFootOffTheGas() {
        thrustState = "DEFAULT"
        time = 0.0f
    }

    fun turnLeft() {
        rotationState = "ROTATELEFT"
        rotationFactor = 1f
        time = 0.0f
    }

    fun turnRight() {
        rotationState = "ROTATERIGHT"
        rotationFactor = -1f
        time = 0.0f
    }

    fun jesusTakeTheWheel() {
        rotationState = "NONE"
        time = 0.0f
        rotationFactor = 0f
    }

    fun isThrusting(): Boolean {
        return thrustState == "THRUSTING"
    }

    fun isTurningLeft() : Boolean {
        return rotationState == "ROTATELEFT"
    }

    fun isTurningRight() : Boolean {
        return rotationState == "ROTATERIGHT"
    }

    fun isJesusSteering() : Boolean {
        return rotationState == "NONE"
    }

    fun startFirin() {
        isFiring = true
    }

    fun stopFirin() {
        isFiring = false
    }

    fun wasHitJustNow() {
        isHit = true
    }

    fun theHitPreviouslyIsHandled() {
        isHit = false
    }


    fun turnLeft(turnSpeed: Float) {
        rotationFactor = turnSpeed
        rotationState = "ROTATELEFT"
    }

    fun turnRight(turnSpeed: Float) {
        rotationFactor = turnSpeed
        rotationState = "ROTATERIGHT"
    }
}