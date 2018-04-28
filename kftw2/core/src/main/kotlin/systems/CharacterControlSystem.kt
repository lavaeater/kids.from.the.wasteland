package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.lavaeater.kftw.components.Box2dBodyComponent
import com.lavaeater.kftw.components.KeyboardControlComponent
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameEvent
import com.lavaeater.kftw.managers.GameStateManager
import ktx.app.KtxInputAdapter
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2
import java.util.*

class CharacterControlSystem(val speed: Float = 20f) :
    KtxInputAdapter,
    IteratingSystem(allOf(KeyboardControlComponent::class, Box2dBodyComponent::class).get(), 45) {

  val gameStateManager = Ctx.context.inject<GameStateManager>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
    val component = kbCtrlMpr[entity]!!
    if (ctrlId != null || ctrlId != component.id) {
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

  var ctrlId: UUID? = null
  var ctrlBody: Body? = null

  override fun keyDown(keycode: Int): Boolean {
    when (keycode) {
      Input.Keys.A, Input.Keys.LEFT -> x = 1f
      Input.Keys.D, Input.Keys.RIGHT -> x = -1f
      Input.Keys.W, Input.Keys.UP -> y = -1f
      Input.Keys.S, Input.Keys.DOWN -> y = 1f
      Input.Keys.I -> gameStateManager.handleEvent(GameEvent.InventoryToggled)
      Input.Keys.M -> gameStateManager.handleEvent(GameEvent.DialogStarted)
    }
    return true
  }

  fun touchYtoScreenY(y: Int): Int {
    return Gdx.graphics.height - 1 - y
  }

  fun touchToVector(touchX: Int, touchY: Int):Vector2 {
    return vec2((Gdx.graphics.width / 2 - touchX).toFloat(), (Gdx.graphics.height / 2 - touchYtoScreenY(touchY)).toFloat()).nor()
  }

  override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

    val dirV = touchToVector(screenX, screenY)

    x = dirV.x
    y = dirV.y
    return true
  }

  override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
    x = 0f
    y = 0f
    return true
  }

  override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
    val dirV = touchToVector(screenX, screenY)

    x = dirV.x
    y = dirV.y
    return true
  }

  override fun keyUp(keycode: Int): Boolean {
    when (keycode) {
      Input.Keys.A, Input.Keys.LEFT -> x = 0f
      Input.Keys.D, Input.Keys.RIGHT -> x = 0f
      Input.Keys.W, Input.Keys.UP -> y = 0f
      Input.Keys.S, Input.Keys.DOWN -> y = 0f
    }
    return true
  }
}