package com.lavaeater

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label

class Player(val name: String = "Player One", val playerId:Int, val controllerInfo: ControllerInfo, var score: Int = 0, var health: Int = 10, var hits: Int = 0, var alive: Boolean = true) {
    var entity: Entity? = null
    fun connectEntity(e: Entity) {
        entity = e
    }

    var scoreLabel: Label = Label(score.toString(), Label.LabelStyle(BitmapFont(), Color.WHITE))
    var healthLabel: Label = Label(health.toString(), Label.LabelStyle(BitmapFont(), Color.WHITE))
    var hitsLabel: Label = Label(hits.toString(), Label.LabelStyle(BitmapFont(), Color.WHITE))
    var position: Int = 1

    fun reset() {
        health = 10
        hits = 0
        position = 1
        updateAlive()
    }

    fun updateLabels() {
        scoreLabel.setText(score.toString())
        healthLabel.setText(health.toString())
        hitsLabel.setText(hits.toString())
    }

    fun takeDamage(damage: Int) {
        health-=damage
        updateAlive()
    }

    fun updateAlive() {
        alive = health > 0
        if(!alive)
            position = Game.instance.players.count() - Game.instance.players.count { !it.key.alive } + 1
    }

    fun heal(healingPoints: Int) {
        health += healingPoints
        updateAlive()
    }
}