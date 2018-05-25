package com.lavaeater.kftw.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.Assets
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.actors.keepWithinParent
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.table
import world.IConversation
import ui.IConversationPresenter
import ui.image
import ui.label
import world.Facts
import world.FactsOfTheWorld

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
  val labelStyle = Label.LabelStyle(Assets.standardFont, Color.WHITE)
  lateinit var scoreLabel :Label


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
    updateScore()
    stage.act(delta)
    stage.draw()
  }

  private var score = 0

  private fun updateScore() {
    val tempScore = FactsOfTheWorld.getIntValue(Facts.Score)
    if(tempScore != score) {
      score = tempScore
      scoreLabel.setText("Score: $score")
    }
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

    setUpScoreBoard()
  }

  private lateinit var rootTable: KTableWidget

  private lateinit var scoreBoard: KTableWidget

  private fun setUpScoreBoard() {
    scoreBoard = table {
        scoreLabel = label("Score: $score", labelStyle) {
        setWrap(true)
        keepWithinParent()
      }.cell(expandY = true, align = Align.bottomLeft, padLeft = 16f, padBottom = 2f)
//      row()
//      image(Assets.beamonHeadshots[antagonistKey]!!) {
//        setScaling(Scaling.fit)
//        keepWithinParent()
//      }.cell(fill = true, width = baseWidth / 3, height = baseWidth / 3, align = Align.bottomLeft,pad = 2f, colspan = 2)
      isVisible = true
      pack()
      background = NinePatchDrawable(Assets.tableBackGround)
    }

    rootTable = table {
      setFillParent(true)
 bottom()
      left()
      add(scoreBoard).align(Align.bottomLeft)
    }

    stage.addActor(rootTable)
  }

  override fun showInventory() {
//    inventoryTable.isVisible = true
  }

  override fun hideInventory() {
//    inventoryTable.isVisible = false
  }
}