package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import com.lavaeater.kftw.injection.Ctx
import ktx.vis.window


class Hud : Disposable {

  var stage: Stage
  private val viewport: Viewport
  val batch = Ctx.context.inject<SpriteBatch>()

  init {
    //No skin needed?
//    Vis
//    Scene2DSkin.defaultSkin =  Skin(Gdx.files.internal("skins/uiskin.json"))
    //setup the HUD viewport using a new camera seperate from gamecam
    //define stage using that viewport and games spritebatch
    viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
    stage = Stage(viewport, batch)
    setup()
  }

  fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined
    stage.draw()
  }

  override fun dispose() {
    VisUI.dispose()
    stage.dispose()
  }

  fun clear() {
    stage.clear()
  }

  fun setup() {
    stage.clear()
    VisUI.load(VisUI.SkinScale.X2) //("tixel/tixel.json")

    val window = window("Data Window") {
      isModal = false
      isMovable = false
      isResizable = false
      height = Gdx.graphics.height.toFloat()
      width = Gdx.graphics.width.toFloat() / 4
      label("This window is for user data")
    }

//    val table = table {
//      label("Just a teststring")
//    }
//    table.setFillParent(true)

    //add table to the stage
    stage.addActor(window)
  }

  fun showInventory() {
    Gdx.app.log("StateMachine","Showing inventory")
  }
}