package com.lavaeater.kftw.screens

import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.ui.IHud
import ktx.app.KtxScreen

class MainGameScreen : KtxScreen {
  private val gameManager = Ctx.context.inject<GameManager>()
  private val hud = Ctx.context.inject<IHud>()

  private fun update(delta:Float) {
    gameManager.update(delta)
    hud.update(delta)
  }

  override fun render(delta: Float) {
    update(delta)
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