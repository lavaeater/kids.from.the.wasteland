package com.lavaeater.kftw.screens

import com.lavaeater.kftw.managers.GameManager
import ktx.app.KtxScreen

class MainGameScreen : KtxScreen {
  private val gameManager = Ctx.context.inject<GameManager>()

  override fun render(delta: Float) {
    gameManager.update(delta)
  }

  override fun resize(width: Int, height: Int) {
    super.resize(width, height)
    gameManager.resize(width, height)
  }

  override fun pause() {
    gameManager.pause()
  }

  override fun dispose() {
    super.dispose()
    gameManager.dispose()
  }
}