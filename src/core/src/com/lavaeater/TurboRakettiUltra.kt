package com.lavaeater

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.gamestate.Context
import com.lavaeater.gamestate.GameScreenState
import com.lavaeater.gamestate.GameState
import com.lavaeater.gamestate.SplashScreenState
import com.lavaeater.screens.MainGameScreen
import com.lavaeater.screens.ScreenDispatcher
import com.lavaeater.screens.StartScreen

class TurboRakettiUltra : Game(), Context {
    lateinit private var batch: SpriteBatch

    lateinit private var screenDispatcher: ScreenDispatcher

    lateinit var am: AssetManager

    override fun newState(state: GameState) {
        when (state)
        {
            is SplashScreenState -> setScreen(startScreen)
            is GameScreenState -> setScreen(mainGameScreen)
        }
    }

    lateinit private var startScreen: StartScreen
    lateinit private var mainGameScreen: MainGameScreen

    override fun create() {
        batch = SpriteBatch()

        am = Assets.load()
        startScreen = StartScreen(batch)
        mainGameScreen = MainGameScreen(batch)

        com.lavaeater.Game.instance.createExecutor(this)
    }

    override fun render() {
        val r = 0 / 255f
        val g = 24f / 255f
        val b = 72f / 255f
        Gdx.gl.glClearColor(r, g, b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        super.render()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        am.dispose()
        startScreen.dispose()
        mainGameScreen.dispose()
    }
}
