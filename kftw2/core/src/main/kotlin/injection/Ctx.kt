package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.PerformanceCounters
import com.lavaeater.kftw.GameSettings
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.managers.BodyFactory
import managers.GameManager
import com.lavaeater.kftw.managers.GameState
import com.lavaeater.kftw.systems.CharacterControlSystem
import map.IMapManager
import map.MapManager
import com.lavaeater.kftw.ui.UserInterface
import com.lavaeater.kftw.ui.IUserInterface
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.MessageManager
import map.TileManager
import world.ConversationManager

class Ctx {

  companion object {
    val context = Context()
    fun buildContext(gameSettings: GameSettings) {
      context.register {
	      bindSingleton(GameState())
        bindSingleton<InputProcessor>(InputMultiplexer())

	      //Character control system with input multiplexer and game state machine
	      bind { CharacterControlSystem(
		        inputProcessor =  this.inject(),
		        gameState = this.inject()) }
        bindSingleton(PerformanceCounters())
        bindSingleton(TileManager())
        bindSingleton(Player(name = "William Hamparsomian"))
        bindSingleton<Batch>(SpriteBatch())
        bindSingleton<Camera>(OrthographicCamera())
        bindSingleton(createWorld())
        bindSingleton(BodyFactory())
        bindSingleton(Engine())
        bindSingleton<IMapManager>(MapManager())
        bindSingleton(ActorFactory())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton<IUserInterface>(UserInterface())
        bindSingleton(ConversationManager())
        bindSingleton(MessageManager())

        //Game manager with characterControl system injected
	      bindSingleton(GameManager(
		        gameSettings,
		        this.provider(),
			      this.inject(),
			      this.inject(),
			      this.inject()))
      }
    }
  }
}