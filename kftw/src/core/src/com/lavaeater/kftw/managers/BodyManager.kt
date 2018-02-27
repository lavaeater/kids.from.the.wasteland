package com.lavaeater.kftw.managers

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.lavaeater.kftw.screens.Ctx
import ktx.box2d.body

class BodyManager {
  val world = Ctx.context.inject<World>()

  fun createBody(width: Float,
                 height: Float,
                 densityIn: Float,
                 position: Vector2,
                 bodyType: BodyDef.BodyType): Body {

    val body = world.body {
      this.position.set(position)
      angle = 0f
      fixedRotation = true
      type = bodyType
      box(width, height) {
        density = densityIn
      }
    }
    return body
  }
}