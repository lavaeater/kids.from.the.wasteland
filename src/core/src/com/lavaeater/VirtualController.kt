package com.lavaeater

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3

class KeyboardController: Controller {
    override fun getAxis(axisCode: Int): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addListener(listener: ControllerListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeListener(listener: ControllerListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccelerometer(accelerometerCode: Int): Vector3 {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAccelerometerSensitivity(sensitivity: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getButton(buttonCode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPov(povCode: Int): PovDirection {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSliderY(sliderCode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSliderX(sliderCode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}