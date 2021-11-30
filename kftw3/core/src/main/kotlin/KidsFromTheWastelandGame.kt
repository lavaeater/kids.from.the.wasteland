package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.kotcrab.vis.ui.VisUI
import Assets
import injection.Ctx
import com.lavaeater.kftw.screens.MainGameScreen
import data.GameSettings
import ktx.app.KtxGame


class KidsFromTheWastelandGame(private val gameSettings: GameSettings = GameSettings()) : KtxGame<Screen>() {

  private lateinit var mainGameScreen: MainGameScreen


  override fun create() {
    Gdx.app.logLevel = Application.LOG_ERROR

    Assets.load(gameSettings)

    VisUI.load(VisUI.SkinScale.X1)
    Ctx.buildContext(gameSettings)
    mainGameScreen = MainGameScreen()
    addScreen(mainGameScreen)
    setScreen<MainGameScreen>()
  }

  override fun dispose() {
    super.dispose()
    VisUI.dispose()
  }
}
