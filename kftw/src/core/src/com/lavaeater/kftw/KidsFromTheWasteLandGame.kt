package com.lavaeater.kftw

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.lavaeater.Assets
import com.lavaeater.kftw.screens.Ctx
import com.lavaeater.kftw.screens.MainGameScreen
import com.lavaeater.kftw.statemachine.BaseEvent
import com.lavaeater.kftw.statemachine.BaseState
import com.lavaeater.kftw.statemachine.StateMachine
import ktx.app.KtxGame

class Initial : BaseState()
class WorldMap : BaseState()
class Combat : BaseState()
class Dialog : BaseState()
class Inventory : BaseState()
class CharacterCreation : BaseState()

class GameStarted : BaseEvent()
class CombatStarted : BaseEvent()
class DialogStarted : BaseEvent()
class FoundSomeLoot: BaseEvent()
class InventoryOpened : BaseEvent()
class GameEnded : BaseEvent()
class CharacterCreated : BaseEvent()
class FoundAreaOfInterest : BaseEvent()

class KidsFromTheWasteLandGame : KtxGame<Screen>() {
  /*
  OK, this baby is gonna be the holder of the GAME STATE MACHINE!

  Every screen should be able to raise some top-leve state events,
  perhaps?

  */
  val gameStateMachine = StateMachine.buildStateMachine(initialStateName = WorldMap()) {
    state(WorldMap()) {
      action {
        setScreen<MainGameScreen>() //When the WorldMap state enters, we show the worldmap screen.
        mainGameScreen.resume()
      }
      edge(FoundSomeLoot(), Inventory()) {
        action { mainGameScreen.pause()}
      }
    }
    state(Inventory()) {
      action {
        mainGameScreen
      }
    }
  }

  private lateinit var mainGameScreen: MainGameScreen

  override fun create() {
    Gdx.app.logLevel = Application.LOG_INFO

    Assets.load()
    Ctx.buildContext()
    mainGameScreen = MainGameScreen()
    addScreen(mainGameScreen)
    setScreen<MainGameScreen>()
  }
}
