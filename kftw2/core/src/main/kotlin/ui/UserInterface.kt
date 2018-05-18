package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import story.IConversation
import ui.IConversationPresenter

class UserInterface(var processInput: Boolean = true): IUserInterface {
  private val batch = Ctx.context.inject<Batch>()
  override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
  override val player = Ctx.context.inject<Player>()
  companion object {
    val aspectRatio = 16/9
    val uiWidth = 800f
    val uiHeight = uiWidth * aspectRatio
  }

  lateinit var conversationUi: IConversationPresenter


  override fun runConversation(conversation: IConversation, conversationEnded: () -> Unit) {
    conversationUi = ConversationPresenter(stage, conversation, {
      conversationUi.dispose()
      conversationEnded()
    })
  }

  init {
    setup()
  }

  override fun update(delta: Float) {
    batch.projectionMatrix = stage.camera.combined
    stage.act(delta)
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
    val inputManager = Ctx.context.inject<InputProcessor>() as InputMultiplexer
    inputManager.addProcessor(stage)
  }

  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}