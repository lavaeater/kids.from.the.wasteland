package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.scene2d.Scene2DSkin
import ktx.vis.table



class Hud(sb: SpriteBatch) : Disposable {

  var stage: Stage
  private val viewport: Viewport

  init {
    Scene2DSkin.defaultSkin =  Skin(Gdx.files.internal("skins/uiskin.json"))
    //setup the HUD viewport using a new camera seperate from gamecam
    //define stage using that viewport and games spritebatch
    viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
    stage = Stage(viewport, sb)
  }

  fun update(dt: Float) {

  }

  override fun dispose() {
    stage.dispose()
  }

  fun clear() {
    stage.clear()
  }

  fun setup() {
    stage.clear()
//    val table = table {
//    }
//    table.setFillParent(true)
//
//    //add table to the stage
//    stage.addActor(table)
  }
}