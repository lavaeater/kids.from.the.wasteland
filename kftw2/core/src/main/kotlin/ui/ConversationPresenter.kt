package com.lavaeater.kftw.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.lavaeater.Assets
import ktx.actors.txt
import ktx.scene2d.label

class ConversationPresenter() : IConversationPresenter {
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
  }

  override fun showNextAnttagonistLine(nextAntagonistLine: String) {
    aLabel.text.append(nextAntagonistLine + "\n\n")
    aLabel.invalidate()
    aLabel.width = aLabel.parent.width //We might need TWO tables... don't know yet
    aLabel.parent.height = aLabel.prefHeight
  }

  val npd = NinePatchDrawable(Assets.speechBubble)
  val speechBubbleStyle = Label.LabelStyle(Assets.standardFont, Color.BLACK).apply { background = npd }

  lateinit var pLabel: Label
  lateinit var aLabel: Label
  lateinit override var table: Table

  init {
    table = ktx.scene2d.table {
      pLabel = label("").apply {
        style = speechBubbleStyle
      }
      aLabel = label("").apply {
        style = speechBubbleStyle
      }
      width = 400f
      x = stage.camera.position.x
      y = stage.camera.position.y
      isVisible = true
    }
  }
}