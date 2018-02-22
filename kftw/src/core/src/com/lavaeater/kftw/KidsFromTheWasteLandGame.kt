package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.managers.GameManager
import ktx.app.KtxGame
import ktx.app.KtxScreen

class GameScreen : KtxScreen {
    val worldManager = GameManager()

    override fun render(delta: Float) {
        worldManager.update(delta)
    }
    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        worldManager.resize(width, height)
    }
    override fun dispose() {
        super.dispose()
        worldManager.dispose()
    }
}

class KidsFromTheWasteLandGame : KtxGame<Screen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_INFO
        Assets.load()
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
