package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.lavaeater.Assets
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.vis.table


class Hud : Disposable {

  var stage: Stage
  private val hudViewPort: Viewport
  val batch = Ctx.context.inject<SpriteBatch>()
  val player = Ctx.context.inject<Player>()
  var inventoryListAdapter : SimpleListAdapter<String>
  lateinit var inventoryTable : Table
  lateinit var listView : ListView<String>

  init {
    VisUI.load(VisUI.SkinScale.X1)
    inventoryListAdapter = SimpleListAdapter(player.gdxInventory).apply {
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

  private var dialogLabel: Any

  fun setup() {
    stage.clear()

    inventoryTable = table {
      debug = true
      height = Gdx.graphics.height.toFloat() / 3
      width = Gdx.graphics.width.toFloat() / 5
      listView = listView(inventoryListAdapter) {
          header = label("Inventory")
        }
      left()
      top()
    }
    stage.addActor(inventoryTable)
    hideInventory()

    dialogLabel = label {
      
    }
  }

  fun showInventory() {
    inventoryTable.isVisible = true
  }

  fun hideInventory() {
    inventoryTable.isVisible = false
  }

  val npd = NinePatchDrawable(Assets.speechBubble)
  val style = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }
  val label = Label("Hello, fool",style)

  fun showDialog() {

    label.x = stage.camera.position.x
    label.y = stage.camera.position.y

    label.isVisible = true
  }
}