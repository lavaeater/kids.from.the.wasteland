package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.screens.MainGameScreen
import ktx.app.KtxGame
import sun.font.ScriptRun


class KidsFromTheWastelandGame : KtxGame<Screen>() {

  private lateinit var mainGameScreen: MainGameScreen
  private lateinit var gameManager: GameManager

  override fun create() {
    Gdx.app.logLevel = Application.LOG_ERROR

    Assets.load()
    Ctx.buildContext()

    gameManager = GameManager(this::setScreen, this::addScreen)


    gameManager.start()
  }

  override fun dispose() {
    super.dispose()
    gameManager.stop()
    gameManager.dispose()

  }
}
