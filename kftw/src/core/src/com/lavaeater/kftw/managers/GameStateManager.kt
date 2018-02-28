package com.lavaeater.kftw.managers

import com.lavaeater.kftw.statemachine.BaseEvent
import com.lavaeater.kftw.statemachine.BaseState
import com.lavaeater.kftw.statemachine.StateMachine

enum class GameState {
  WorldMap,
  Combat,
  Dialog,
  Inventory,
  CharacterScreen
}

enum class GameEvent {
  GameStarted,
  CombatStarted,
  CombatEnded,
  DialogStarted,
  DialogEnded,
  LootFound,
  InventoryOpened,
  InventoryClosed,
  GamePaused,
  CharacterScreenOpened,
  CharacterScreenClosed
}

class GameStateManager(private val stateChange: (newState: GameState)-> Unit) {

  fun handleEvent(event: GameEvent) {
    gameStateMachine.acceptEvent(event)
  }

  val gameStateMachine = StateMachine.buildStateMachine<GameState, GameEvent>(GameState.WorldMap, stateChange) {
    state(GameState.WorldMap) {
      edge(GameEvent.LootFound, GameState.Inventory) {}
      
    }
    state(GameState.Inventory) {
      edge(GameEvent.InventoryClosed, GameState.WorldMap) {}
    }
  }
}