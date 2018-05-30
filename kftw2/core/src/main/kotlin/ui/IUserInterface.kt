package ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import story.IConversation

interface IUserInterface : Disposable {
  val stage: Stage
  val hudViewPort: Viewport
  fun showInventory()
  fun hideInventory()
  fun update(delta: Float)
  override fun dispose()
  fun clear()
  fun runConversation(conversation: IConversation, conversationEnded: () -> Unit)
	fun showSplashScreen()
}