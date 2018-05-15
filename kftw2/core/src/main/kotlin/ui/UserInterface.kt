package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import story.IConversation
import ui.IConversationPresenter

class UserInterface: IUserInterface {
  private val batch = Ctx.context.inject<Batch>()
  override val hudViewPort = ExtendViewport(640f, 480f, OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
  override val player = Ctx.context.inject<Player>()

  lateinit var conversationUi: IConversationPresenter


  override fun runConversation(conversation: IConversation, conversationEnded: () -> Unit) {
    var currentInputProcessor = Gdx.input.inputProcessor

    conversationUi = ConversationPresenter(stage, conversation, {
      Gdx.input.inputProcessor = currentInputProcessor
      conversationUi.dispose()
      conversationEnded()
    })
  }



//  private val inventoryListAdapter  = SimpleListAdapter(player.inventory.toGdxArray()).apply {
//      selectionMode = AbstractListAdapter.SelectionMode.SINGLE
//  }

//  val inventoryTable = table {
//      debug = true
//      height = Gdx.graphics.height.toFloat() / 3
//      width = Gdx.graphics.width.toFloat() / 5
//      listView(inventoryListAdapter) {
//          header = label("Inventory")
//      }
//      left()
//      top()
//  }



  init {
    setup()
  }

  override fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined
    stage.draw()
  }

  override fun dispose() {
    stage.dispose()
  }

  override fun clear() {
    stage.clear()
  }


  private fun setup() {
    stage.clear()

//    stage.addActor(inventoryTable)
//    hideInventory()
//
//    stage.addActor(conversationTable)
  }

  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}