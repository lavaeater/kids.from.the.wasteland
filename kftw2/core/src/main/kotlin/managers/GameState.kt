package com.lavaeater.kftw.managers

import com.lavaeater.kftw.statemachine.StateMachine

enum class GameStates {
  WorldMap,
  Combat,
  Dialog,
  Inventory,
  CharacterScreen
}

enum class GameEvents {
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

class GameState() {

  private val changeListeners = mutableSetOf<(GameStates)->Unit>()
  fun addChangeListener(listener: (GameStates) ->Unit) {
    changeListeners.add(listener)
  }

  fun removeListener(listener: (GameStates) -> Unit) {
    changeListeners.remove(listener)
  }

  fun handleEvent(event: GameEvents) {
    stateMachine.acceptEvent(event)
  }

  fun stateChange(newState: GameStates) {
    for (listener in changeListeners)
      listener(newState)
  }

  private val stateMachine : StateMachine<GameStates, GameEvents> = StateMachine.buildStateMachine(GameStates.WorldMap, ::stateChange) {
    state(GameStates.WorldMap) {
      edge(GameEvents.LootFound, GameStates.Inventory) {}
      edge(GameEvents.InventoryToggled, GameStates.Inventory) {}
      edge(GameEvents.DialogStarted, GameStates.Dialog) {}
    }
    state(GameStates.Inventory) {
      edge(GameEvents.InventoryToggled, GameStates.WorldMap) {}
    }
    state(GameStates.Dialog) {
      edge(GameEvents.DialogEnded, GameStates.WorldMap) {}
    }
  }

  init {
    stateMachine.initialize()
  }
}