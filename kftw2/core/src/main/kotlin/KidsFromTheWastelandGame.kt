package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.screens.MainGameScreen
import ktx.app.KtxGame


class KidsFromTheWastelandGame : KtxGame<Screen>() {

  private lateinit var mainGameScreen: MainGameScreen

  override fun create() {
    Gdx.app.logLevel = Application.LOG_ERROR

    Assets.load()
    Ctx.buildContext()
    mainGameScreen = MainGameScreen()
    addScreen(mainGameScreen)
    setScreen<MainGameScreen>()
  }
}