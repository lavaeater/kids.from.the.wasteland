package com.lavaeater.systems

import com.badlogic.ashley.core.EntitySystem
import com.lavaeater.Game

/**
 * Created by 78899 on 2017-08-24.
 */

class GameStateSystem (val gameOver: () -> Unit) : EntitySystem(500) {
    override fun update(deltaTime: Float) {

        if(Game.instance.players.keys.filter { it.alive }.count() == 1)
           gameOver()
    }
}