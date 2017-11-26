package com.lavaeater

import com.badlogic.gdx.controllers.Controller
import com.lavaeater.gamestate.*
import org.softpark.stateful4k.StateMachine
import org.softpark.stateful4k.action.IExecutor
import org.softpark.stateful4k.config.IConfigurator
import org.softpark.stateful4k.extensions.createExecutor
import org.softpark.stateful4k.extensions.event
import org.softpark.stateful4k.extensions.state

/**
 * Created by 78899 on 2017-07-31.
 */
class Game private constructor() {
    val players = HashMap<Player, ControllerInfo>()

    private val config: IConfigurator<Context, GameState, GameEvent> = StateMachine.createConfigurator<Context, GameState, GameEvent>()

    lateinit var executor: IExecutor<Context, GameState, GameEvent>

    private object Holder { val INSTANCE = Game()}
    companion object {
        val instance: Game by lazy { Holder.INSTANCE }
    }

    fun createExecutor(context: Context) {
        with(config) {
            event(SplashScreenState::class, GameStartEvent::class)
                    .goto { GameScreenState() }
            event(GameScreenState::class, GameOverEvent::class)
                    .goto { SplashScreenState() }

            state(SplashScreenState::class)
                    .enter { context.newState(state) }
            state(GameScreenState::class)
                    .enter { context.newState(state) }
        }
        executor = config.createExecutor(context, SplashScreenState())
    }

    fun numberOfPlayers(): Int {
        return players.keys.count()
    }

    fun hasPlayer(controller: Controller): Boolean {
        return players.values.filter { !it.isKeyBoardController && it.controller!! == controller }.any()
    }

    fun hasPlayer(p:Int):Boolean {
        return players.count() >= p
    }

    fun hasPlayerOne():Boolean {
        return hasPlayer(1)
    }

    fun hasPlayerTwo():Boolean {
        return hasPlayer(2)
    }

    fun hasPlayerThree():Boolean {
        return hasPlayer(3)
    }

    fun hasPlayerFour():Boolean {
        return hasPlayer(4)
    }

    fun addPlayer(controller: Controller) {
        if(players.keys.count() < 4 && !players.values.filter { it.controller == controller}.any()) {
            var nextPlayerId = players.keys.count() + 1
            var newPlayer = Player("Player " + nextPlayerId, nextPlayerId, ControllerInfo(controller = controller))
            players.put(newPlayer, newPlayer.controllerInfo)
        }
    }

    fun removePlayer(controller: Controller) {
        if(players.values.filter { !it.isKeyBoardController && it.controller == controller}.any()) {
            val key = players.filter {it.value.controller == controller }.keys.first()
            players.remove(key)
        }
    }

    fun updatePlayerLabels() {
        for(player in players.keys) {
            player.updateLabels()
        }
    }

    fun getPlayer(p: Int): Player {
        return players.keys.filter { it.playerId == p }.single()
    }

    fun getPlayer(controller: Controller) :Player {
        return players.filter { it.value.controller == controller }.keys.first()
    }

    fun hasKeyboardPlayer(): Boolean {
        return players.filter { it.value.isKeyBoardController}.any()
    }

    fun addKeyboardPlayer() {
        if(players.keys.count() < 4 && !players.filter{ it.value.isKeyBoardController }.any() ) {
            var nextPlayerId = players.keys.count() + 1
            var newPlayer = Player("Player " + nextPlayerId, nextPlayerId, ControllerInfo(true))
            players.put(newPlayer, newPlayer.controllerInfo)
        }
    }

    fun removeKeyboardPlayer() {
        if(players.values.filter { it.isKeyBoardController }.any()) {
            val key = players.filter {it.value.isKeyBoardController}.keys.first()
            players.remove(key)
        }
    }
}

