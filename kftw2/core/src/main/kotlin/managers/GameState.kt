package managers

import statemachine.StateMachine

enum class GameStates {
  WorldMap,
  Combat,
  Dialog,
  Inventory,
  CharacterScreen,
  NotStarted,
  SplashScreen
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
  CharacterScreenClosed,
  GameResumed
}

class GameState {

  private val changeListeners = mutableSetOf<(GameStates)->Unit>()
  fun addChangeListener(listener: (GameStates) ->Unit) {
    changeListeners.add(listener)
  }

  fun start() {
    stateMachine.initialize()
  }

  fun removeListener(listener: (GameStates) -> Unit) {
    changeListeners.remove(listener)
  }

  fun handleEvent(event: GameEvents) {
    stateMachine.acceptEvent(event)
  }

  private fun stateChange(newState: GameStates) {
    for (listener in changeListeners)
      listener(newState)
  }

  private val stateMachine : StateMachine<GameStates, GameEvents> = StateMachine.buildStateMachine(GameStates.SplashScreen, ::stateChange) {
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
    state(GameStates.SplashScreen) {
      edge(GameEvents.GameResumed, GameStates.WorldMap) {}
    }
  }
}