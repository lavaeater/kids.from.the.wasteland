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
  InventoryToggled,
  GamePaused,
  CharacterScreenOpened,
  CharacterScreenClosed
}

class GameStateManager() {

  val changeListeners = mutableSetOf<(GameState)->Unit>()
  fun addChangeListener(listener: (GameState) ->Unit) {
    changeListeners.add(listener)
  }

  fun removeListener(listener: (GameState) -> Unit) {
    changeListeners.remove(listener)
  }

  fun handleEvent(event: GameEvent) {
    gameStateMachine.acceptEvent(event)
  }

  fun stateChange(newState: GameState) {
    for (listener in changeListeners)
      listener(newState)
  }

  private val gameStateMachine : StateMachine<GameState, GameEvent> = StateMachine.buildStateMachine(GameState.WorldMap, ::stateChange) {
    state(GameState.WorldMap) {
      edge(GameEvent.LootFound, GameState.Inventory) {}
      edge(GameEvent.InventoryToggled, GameState.Inventory) {}
        edge(GameEvent.DialogStarted, GameState.Dialog) {}
    }
    state(GameState.Inventory) {
      edge(GameEvent.InventoryToggled, GameState.WorldMap) {}
    }
      state(GameState.Dialog) {
          edge(GameEvent.DialogEnded, GameState.WorldMap) {}
      }
  }

  init {
    gameStateMachine.initialize()
  }
}