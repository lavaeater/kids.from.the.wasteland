package com.lavaeater.kftw.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.lavaeater.kftw.data.IAgent
import world.IConversation

interface IUserInterface : Disposable {
  val stage: Stage
  val hudViewPort: Viewport
  val player: IAgent
  fun showInventory()
  fun hideInventory()
  fun update(delta: Float)
  override fun dispose()
  fun clear()
  fun runConversation(conversation: IConversation, conversationEnded: () -> Unit)
}