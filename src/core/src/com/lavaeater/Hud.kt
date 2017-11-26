package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.table

class Hud(sb: SpriteBatch) : Disposable {

    var stage: Stage
    private val viewport: Viewport

    //score && time tracking variables
    var isTimeUp: Boolean = false
        private set

    private val playerLabels = HashMap<Int, ArrayList<Label>>()

    init {
        Scene2DSkin.defaultSkin =  Skin(Gdx.files.internal("data/uiskin.json"))
        //setup the HUD viewport using a new camera seperate from gamecam
        //define stage using that viewport and games spritebatch
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), OrthographicCamera())
        stage = Stage(viewport, sb)
    }

    fun update(dt: Float) {

        //Do update stuff if necessary
//        timeCount += dt
//        if (timeCount >= 1) {
//            if (worldTimer > 0) {
//                worldTimer--
//            } else {
//                isTimeUp = true
//            }
//            countdownLabel.setText(String.format("%03d", worldTimer))
//            timeCount = 0f
//        }
    }

    override fun dispose() {
        stage.dispose()
    }

    fun clear() {
        stage.clear()
    }

    fun setup() {
        stage.clear()
        var playerIndex = 1
        var align = 0
        val table = table {
            for (player in Game.instance.players.keys) {
                when (playerIndex) {
                    1 -> align = Align.topLeft
                    2 -> align = Align.topRight
                    3 -> align = Align.bottomLeft
                    4 -> align = Align.bottomRight
                }
                table {
                    label(player.name) {
                        style = Label.LabelStyle(BitmapFont(), Color.WHITE)
                    }
                    row()
                    player.scoreLabel = label(player.score.toString()) {
                        style = Label.LabelStyle(BitmapFont(), Color.WHITE)
                    }
                    row()
                    player.healthLabel = label(player.health.toString()) {
                        style = Label.LabelStyle(BitmapFont(), Color.WHITE)
                    }
                    row()
                    player.hitsLabel = label(player.hits.toString()) {
                        style = Label.LabelStyle(BitmapFont(), Color.WHITE)
                    }
                }.inCell.expand().align(align)
                if(playerIndex % 2 == 0) {
                    row()
                }
                playerIndex++
            }
        }
        table.setFillParent(true)

        //add table to the stage
        stage.addActor(table)
    }

//    companion object {
//        var score: Int? = null
//            private set
//
//        fun addScore(value: Int) {
//            score += value
//            scoreLabel.setText(String.format("%06d", score))
//        }
//    }
}