package com.lavaeater.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Game
import com.lavaeater.Hud
import com.lavaeater.managers.MainGameManager
import com.lavaeater.gamestate.GameOverEvent

/**
 * Created by barry on 12/9/15 @ 11:12 PM.
 */
class MainGameScreen(val batch: SpriteBatch) : ScreenAdapter() {
    var isInitialized = false
    var elapsedTime = 0f
    val mainGameManager: MainGameManager by lazy { MainGameManager(batch = batch, gameOver = this::gameOver) }
    private val hud = Hud(batch)

    private fun init() {
        Gdx.app.log("GameScreen", "Initializing")
        isInitialized = false


        isInitialized = true
    }



    private fun update(delta: Float) {
        mainGameManager.update(delta)
        hud.update(delta)
        batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        elapsedTime += delta
    }

    override fun show() {
        super.show()
        //add code for all playable entities, I guess?
        if(!isInitialized)
            init()

        initWorldAndPlayers()
    }

    override fun resize(width: Int, height: Int) {
        mainGameManager.resize(width, height)
    }

    override fun hide() {
        super.hide()
        clearWorld()
    }

    override fun render(delta: Float) {
        update(delta)
    }

    fun gameOver(): Unit {
        Game.instance.executor.fire(GameOverEvent())
    }

    fun initWorldAndPlayers() {

        mainGameManager.createMap()

        val startPositions = mainGameManager.map.mapObjects.filter { it.isStartPoint }

        //val entity = mainGameManager.createPlayer(startPositions.first())
        //mainGameManager.addInputSystem(entity)

//        val player2 = mainGameManager.createPlayer(startPositions.last())

        var startPos = 0
        for ((player, controller) in Game.instance.players) {
            player.reset()
            mainGameManager.createPlayer(startPositions[startPos], player, controller)
            startPos++
        }

        mainGameManager.processAllSystems()

        hud.setup()
    }

    private fun clearWorld() {
        //clear the HUD
        hud.clear()

        mainGameManager.clearWorld()
        mainGameManager.stopProcessing()



    }
}

