package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.kotcrab.vis.ui.VisUI
import com.lavaeater.Assets
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.screens.MainGameScreen
import ktx.app.KtxGame
import screens.BoxScreen
import screens.PortraitScreen


class KidsFromTheWastelandGame : KtxGame<Screen>() {

  private lateinit var mainGameScreen: MainGameScreen

  override fun create() {
    Gdx.app.logLevel = Application.LOG_ERROR

    Assets.load()

    VisUI.load(VisUI.SkinScale.X1)
    Ctx.buildContext()
    mainGameScreen = MainGameScreen()
    addScreen(mainGameScreen)
    setScreen<MainGameScreen>()
  }

  override fun dispose() {
    super.dispose()
    VisUI.dispose()
  }
}
