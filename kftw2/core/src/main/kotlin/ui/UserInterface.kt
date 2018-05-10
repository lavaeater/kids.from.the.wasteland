package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxInputAdapter
import story.ConversationState
import story.IConversation

class UserInterface : IUserInterface {

  private val batch = Ctx.context.inject<Batch>()
  override val hudViewPort = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
  override val player = Ctx.context.inject<Player>()


  override fun runConversation(conversation: IConversation, conversationEnded: () -> Unit) {
    var currentInputProcessor = Gdx.input.inputProcessor
    Gdx.input.inputProcessor = object : KtxInputAdapter {
      override fun keyDown(keycode: Int): Boolean {
        when(conversation.state) {
          ConversationState.ProtagonistMustChoose -> {
            if(keycode !in 7..16) return true//Not a numeric key!
            val index = keycode - 7
            if(index !in 0..conversation.choiceCount - 1) return true//Out of range for correct choices, just ignore

            conversation.makeChoice(index)
          }
          else -> return true
        }
        return true
      }
    }

    val conversationUi = ConversationPresenter(stage)

    while (conversation.state != ConversationState.Ended) {
      while(conversation.state == ConversationState.AntagonistHasMoreToSay) {
        conversationUi.showNextAnttagonistLine(conversation.getNextAntagonistLine())
        Thread.sleep(2000) //Here or in presenter?
      }

      if(conversation.state == ConversationState.ProtagonistMustChoose) {
        conversationUi.showProtagonistChoices(conversation.getProtagonistChoices())
      }

      /*
      After getting the choices, the conversation enters
      ProtagonistMustChoose state, which means nothing more happens
      until the user selects something
       */

      if(conversation.state == ConversationState.Ended)
        break
    }

    conversationUi.dispose()
    Gdx.input.inputProcessor = currentInputProcessor

    conversationEnded() //The callback to i.e the conversationManager that will start the app again etc. Could be a message.
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