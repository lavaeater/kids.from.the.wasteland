package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxInputAdapter

class KeyboardCameraControlSystem(val camera: OrthographicCamera):
        KtxInputAdapter,
        EntitySystem(299) {

    val speed = 0.5f
    var y = 0f;
    var x = 0f

    override fun update(deltaTime: Float) {
        camera.position.x += x * speed
        camera.position.y += y * speed
        camera.update(true)
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> x = -1f
            Input.Keys.D -> x = 1f
            Input.Keys.W -> y = 1f
            Input.Keys.S -> y = -1f
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