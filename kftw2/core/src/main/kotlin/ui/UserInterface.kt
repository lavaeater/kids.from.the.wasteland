package com.lavaeater.kftw.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lavaeater.Assets
import com.lavaeater.kftw.data.IAgent
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxInputAdapter
import ktx.scene2d.table
import ktx.scene2d.label
import story.IConversation

interface IUserInterface : Disposable {
    val stage: Stage
    val hudViewPort : Viewport
    val player: IAgent
    fun showInventory()
    fun hideInventory()
    fun startDialog(madeChoice: (Int) -> Unit)
    fun showDialog(
            lines: Iterable<String>,
            x:Float = stage.camera.position.x,
            y:Float = stage.camera.position.y)

    fun showChoices(
            choices: Iterable<String>,
            x: Float = stage.camera.position.x,
            y: Float = stage.camera.position.y)

    fun hideDialog()
    fun update(delta: Float)
    override fun dispose()
    fun clear()

  fun runConversation(conversation: IConversation, conversationEnded:()->Unit)
}

class UserInterface : IUserInterface {

  private val batch = Ctx.context.inject<Batch>()
  override val hudViewPort = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
  override val stage = Stage(hudViewPort, batch)
  override val player = Ctx.context.inject<Player>()

  override fun runConversation(conversation: IConversation, conversationEnded: () -> Unit) {
    var currentInputProcessor = Gdx.input.inputProcessor
    Gdx.input.inputProcessor = object : KtxInputAdapter {
      override fun keyDown(keycode: Int): Boolean {
        return super.keyDown(keycode)
      }
    }

    val npd = NinePatchDrawable(Assets.speechBubble)
    val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }
//      val label = Label("Hello, fool",speechBubbleStyle).apply {
//        setFillParent(true)
//        setWrap(true)
//      }
    val table = table {
      val pLabel = label("").apply {
        style = speechBubbleStyle
      }
      val aLabel = label("").apply {
        style = speechBubbleStyle
      }
      width = 400f
      x = stage.camera.position.x
      y = stage.camera.position.y
      isVisible = true
    }
    stage.addActor(table)

    while (conversation.canContinue) {
    }
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

  override fun startDialog(madeChoice:(Int) -> Unit) {
    choiceHandler = madeChoice
    Gdx.input.inputProcessor = object: KtxInputAdapter{
      override fun keyDown(keycode: Int): Boolean {
        choiceHandler?.invoke(keycode)
        return true
      }
    }
  }

  override fun showDialog(lines:Iterable<String>,
                          x: Float,
                          y: Float) {
//    conversationTable.x = x
//    conversationTable.y = y
//    label.txt = ""
//
//    label.isVisible = true
//    conversationTable.isVisible = true
//    Timer.instance().clear()
//
//    Timer.instance().scheduleTask(object: Timer.Task(){
//      var i = 0
//      override fun run() {
//        label.text.append(lines.elementAt(i)+"\n\n")
//        label.invalidate()
//        label.width = label.parent.width
//        label.parent.height = label.prefHeight
//        i++
//      }
//    },0f, 2f, lines.count()-1)
  }

  var choiceCount = 0
  var choiceHandler: ((Int)->Unit)? = null
  override fun showChoices(
      choices: Iterable<String>,
      x: Float,
      y: Float) {
//    conversationTable.x = x
//    conversationTable.y = y
//    Timer.instance().clear()
//
//    choiceCount = choices.count()
//    var choiceText = ""
//    for((i, line) in choices.withIndex()) {
//      choiceText += "$i: " + line + "\n\n"
//    }
//    label.txt = ""
//    label.txt = choiceText
//    label.invalidate()
//    label.width = label.parent.width
//    label.parent.height = label.prefHeight
//    label.isVisible = true
//    conversationTable.isVisible = true
  }

  override fun hideDialog() {
//    label.isVisible = false
//    conversationTable.isVisible = false
  }
}