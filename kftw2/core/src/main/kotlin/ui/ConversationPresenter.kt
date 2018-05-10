package com.lavaeater.kftw.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.Assets
import ktx.actors.txt
import ktx.scene2d.*

class ConversationPresenter(val s: Stage) : IConversationPresenter, Disposable {
  override fun dispose() {
    table.remove()
  }

  override fun showProtagonistChoices(protagonistChoices: Iterable<String>) {
    var choiceText = ""
    for ((i, line) in protagonistChoices.withIndex()) {
      choiceText += "$i: " + line + "\n\n"
    }
    pLabel.txt = ""
    pLabel.txt = choiceText
    pLabel.invalidate()
    pLabel.width = pLabel.parent.width
    pLabel.parent.height = pLabel.prefHeight
    pLabel.isVisible = true
  }

  override fun showNextAnttagonistLine(nextAntagonistLine: String) {
    aLabel.text.append(nextAntagonistLine + "\n\n")
    aLabel.invalidate()
    aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
    aLabel.parent.height = aLabel.prefHeight
    aLabel.isVisible = true
  }

  private val npd = NinePatchDrawable(Assets.speechBubble)
  private val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }

  private lateinit var pLabel: Label
  private lateinit var aLabel: Label
  private var table: Table

  init {
    table = ktx.scene2d.table {
      pLabel = label("", speechBubbleStyle)
      aLabel = label("", speechBubbleStyle)
      width = 400f
      x = s.camera.position.x
      y = s.camera.position.y
      isVisible = true
      setDebug(true)
    }
    s.addActor(table)
  }
}

inline fun <S> KWidget<S>.label(
    text: CharSequence,
    style: Label.LabelStyle,
    init: (@Scene2dDsl Label).(S) -> Unit = {}) = actor(Label(text, style), init)