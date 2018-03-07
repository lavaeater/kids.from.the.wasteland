package com.lavaeater.kftw.managers

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

  private val gameStateMachine : StateMachine<GameState, GameEvent> = StateMachine.buildStateMachine(GameState.WorldMap, stateChange) {
    state(GameState.WorldMap) {
      edge(GameEvent.LootFound, GameState.Inventory) {}
      edge(GameEvent.InventoryOpened, GameState.Inventory) {}
    }
    state(GameState.Inventory) {
      edge(GameEvent.InventoryClosed, GameState.WorldMap) {}
    }
  }

  init {
    gameStateMachine.initialize()
  }
}