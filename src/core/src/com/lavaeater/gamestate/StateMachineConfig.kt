package com.lavaeater.gamestate

/**
 * Created by tommie on 2017-08-26.
 */

interface GameState
class GameScreenState: GameState
class SplashScreenState: GameState
class TestGameState:GameState


interface GameEvent
class GameStartEvent : GameEvent
class GameOverEvent : GameEvent
class TestGameEvent: GameEvent

interface Context {
    fun newState(state: GameState)
}
