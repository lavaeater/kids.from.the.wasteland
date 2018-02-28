package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.screens.Ctx
import com.lavaeater.kftw.screens.MainGameScreen
import com.lavaeater.kftw.statemachine.BaseEvent
import com.lavaeater.kftw.statemachine.BaseState
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.app.KtxGame


class KidsFromTheWasteLandGame : KtxGame<Screen>() {
  /*
  OK, this baby is gonna be the holder of the GAME STATE MACHINE!

  Every screen should be able to raise some top-leve state events,
  perhaps?

  */

  private lateinit var mainGameScreen: MainGameScreen

  override fun create() {
    Gdx.app.logLevel = Application.LOG_INFO

    Assets.load()
    Ctx.buildContext()
    mainGameScreen = MainGameScreen()
    addScreen(mainGameScreen)
    setScreen<MainGameScreen>()
  }
}
