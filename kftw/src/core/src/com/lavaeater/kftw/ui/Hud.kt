package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.vis.*


class Hud : Disposable {

  var stage: Stage
  private val hudViewPort: Viewport
  val batch = Ctx.context.inject<SpriteBatch>()
  val player = Ctx.context.inject<Player>()
  var inventoryListAdapter : SimpleListAdapter<String>
  lateinit var inventoryTable: Table

  init {
    VisUI.load("tixel/tixel.json")
    inventoryListAdapter = SimpleListAdapter(player.inventory).apply {
      selectionMode = AbstractListAdapter.SelectionMode.SINGLE
    }
    hudViewPort = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
    stage = Stage(hudViewPort, batch)
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

    val window = window("") {
      isModal = false
      isMovable = false
      isResizable = false
      height = Gdx.graphics.height.toFloat() / 6
      width = Gdx.graphics.width.toFloat()
      horizontalFlowGroup {
        inventoryTable = table (true){
          setFillParent(true)
          listView(inventoryListAdapter) {
            header = label("Inventory")
          }
        }
      }
    }
    stage.addActor(window)
    hideInventory()
  }

  fun showInventory() {
    inventoryTable.isVisible = true
  }

  fun hideInventory() {
    inventoryTable.isVisible = false
  }
}