package com.lavaeater.kftw

import com.lavaeater.kftw.managers.WorldManager
import ktx.app.KtxScreen

class GameScreen : KtxScreen {
  private val worldManager = WorldManager()

  override fun render(delta: Float) {
    worldManager.update(delta)
  }

  override fun resize(width: Int, height: Int) {
    super.resize(width, height)
    worldManager.resize(width, height)
  }

  override fun pause() {
    worldManager.pause()
  }

  override fun dispose() {
    super.dispose()
    worldManager.dispose()
  }
}