package com.lavaeater.kftw.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table

interface IConversationPresenter {
  fun showNextAnttagonistLine(nextAntagonistLine: String)
  fun showProtagonistChoices(protagonistChoices: Iterable<String>)

  var table: Table
}