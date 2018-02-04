package com.lavaeater.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Game
import com.lavaeater.Hud
import com.lavaeater.managers.MainGameManager
import com.lavaeater.gamestate.GameOverEvent
import com.lavaeater.managers.WorldManager

/**
 * Created by barry on 12/9/15 @ 11:12 PM.
 */
class WorldMapScreen(val batch: SpriteBatch) : ScreenAdapter() {
    var isInitialized = false
    var elapsedTime = 0f
    val worldManager: WorldManager by lazy { WorldManager(batch = batch, gameOver = this::gameOver) }
    private val hud = Hud(batch)

    private fun init() {
        Gdx.app.log("GameScreen", "Initializing")
        isInitialized = true
    }

    private fun update(delta: Float) {
        worldManager.update(delta)
        hud.update(delta)
        batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        elapsedTime += delta
    }

    override fun show() {
        super.show()
        if(!isInitialized)
            init()

        initWorldAndPlayers()
    }

    override fun resize(width: Int, height: Int) {
        worldManager.resize(width, height)
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
        worldManager.createMap()
        val startPositions = worldManager.map.mapObjects.filter { it.isStartPoint }
        var startPos = 0
        for ((player, controller) in Game.instance.players) {
            player.reset()
            worldManager.createPlayer(startPositions[startPos], player, controller)
            startPos++
        }

        worldManager.processAllSystems()
        hud.setup()
    }

    private fun clearWorld() {
        //clear the HUD
        hud.clear()

        worldManager.clearWorld()
        worldManager.stopProcessing()
    }
}