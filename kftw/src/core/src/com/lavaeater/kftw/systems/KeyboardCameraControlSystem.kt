package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxInputAdapter
import javax.print.DocFlavor

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE
}

class KeyboardCameraControlSystem(val camera: OrthographicCamera):
        KtxInputAdapter,
        EntitySystem(299) {

    val directions =
            mapOf(
                    Input.Keys.A to Direction.LEFT,
                    Input.Keys.D to Direction.RIGHT,
                    Input.Keys.W to Direction.UP,
                    Input.Keys.S to Direction.DOWN
            )
    var currentDirection: Direction = Direction.NONE
    override fun update(deltaTime: Float) {
        when(currentDirection) {
            Direction.UP -> camera.position.y--
            Direction.DOWN -> camera.position.y++
            Direction.LEFT -> camera.position.x--
            Direction.RIGHT -> camera.position.x++
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A -> currentDirection = Direction.LEFT
            Input.Keys.D -> currentDirection = Direction.RIGHT
            Input.Keys.W -> currentDirection = Direction.UP
            Input.Keys.S -> currentDirection = Direction.DOWN
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.A,
            Input.Keys.D,
            Input.Keys.W,
            Input.Keys.S -> currentDirection = Direction.NONE
        }
        return true
    }
}