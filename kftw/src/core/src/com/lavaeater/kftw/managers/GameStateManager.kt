package com.lavaeater.kftw.managers

import com.lavaeater.kftw.screens.MainGameScreen
import com.lavaeater.kftw.statemachine.BaseEvent
import com.lavaeater.kftw.statemachine.BaseState
import com.lavaeater.kftw.statemachine.StateMachine
import com.lavaeater.kftw.statemachine.State

enum class GameState {
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
  InventoryOpened,
  InventoryClosed,
  GamePaused,
  CharacterScreenOpened,
  CharacterScreenClosed
}

class GameStateManager(stateChange: (newState: State)-> Unit) {

  fun handleEvent(event: BaseEvent) {
    gameStateMachine.acceptEvent(event)
  }

  val gameStateMachine = StateMachine.buildStateMachine(initialStateName = WorldMap()) {
    state(WorldMap()) {
      action {
        stateChange(it) //Maybe some other stuff some day
      }
      edge(FoundSomeLoot(), Inventory()) {
      }
    }
    state(Inventory()) {
      action {
        stateChange(it)
      }
      edge(InventoryClosed(), WorldMap())
    }
  }
}

class InventoryClosed : BaseEvent()
