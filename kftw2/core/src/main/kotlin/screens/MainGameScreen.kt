package com.lavaeater.kftw.screens

import com.badlogic.gdx.Gdx
import com.lavaeater.kftw.injection.Ctx
import managers.GameManager
import com.lavaeater.kftw.ui.IUserInterface
import ktx.app.KtxScreen

class MainGameScreen : KtxScreen {
  private val gameManager = Ctx.context.inject<GameManager>()
  private val hud = Ctx.context.inject<IUserInterface>()
  init {
  	Gdx.input.inputProcessor = Ctx.context.inject()
  }

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