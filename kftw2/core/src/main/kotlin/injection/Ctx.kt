package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.PerformanceCounters
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lavaeater.kftw.GameSettings
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.managers.BodyFactory
import managers.GameManager
import com.lavaeater.kftw.managers.GameState
import com.lavaeater.kftw.managers.Messages
import com.lavaeater.kftw.systems.*
import map.IMapManager
import map.MapManager
import com.lavaeater.kftw.ui.UserInterface
import com.lavaeater.kftw.ui.IUserInterface
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.MessageSwitch
import map.TileManager
import world.ConversationManager

class Ctx {

  companion object {
    val context = Context()

	  fun getEngine(context: Context) : Engine {
		  return Engine().apply {
			  addSystem(CharacterControlSystem(
					  inputProcessor = context.inject(),
					  gameState = context.inject()))
			  addSystem(NpcControlSystem())
			  addSystem(RenderMapSystem(false))
			  addSystem(RenderCharactersSystem())
			  addSystem(AiSystem())
			  addSystem(PhysicsSystem())
//			  addSystem(PhysicsDebugSystem())
			  addSystem(WorldFactsSystem())

		  }
	  }

    fun buildContext(gameSettings: GameSettings) {
      context.register {
	      bindSingleton(GameState())
        bindSingleton<InputProcessor>(InputMultiplexer())
        bindSingleton(PerformanceCounters())
        bindSingleton(TileManager())
        bindSingleton(Player(name = "William Hamparsomian"))
        bindSingleton<Batch>(SpriteBatch())
        bindSingleton<Camera>(OrthographicCamera())

	      //Bind provider for a viewport with the correct settings for this game!
	      bind<Viewport> {
		      ExtendViewport(gameSettings.width,
				      gameSettings.height,
				      this.inject())
	      }
        bindSingleton(createWorld())
        bindSingleton(BodyFactory())
        bindSingleton<IMapManager>(MapManager())
	      bindSingleton(getEngine(this))
	      bind { ActorFactory() }
        bindSingleton<IUserInterface>(UserInterface())
        bindSingleton(ConversationManager())
        bindSingleton<Telegraph>(MessageSwitch())
	      bindSingleton<MessageDispatcher>(
			      com.badlogic.gdx.ai.msg.MessageManager
					      .getInstance().apply {
		      addListener(this@register.inject(), Messages.CollidedWithImpassibleTerrain)
		      addListener(this@register.inject(), Messages.PlayerMetSomeone)
	      })

	      bindSingleton(GameManager(
		        gameSettings,
			      this.inject(),
			      this.inject(),
			      this.inject(),
			      this.provider(),
			      this.inject(),
			      this.provider(),
			      this.inject(),
			      this.inject(),
			      this.inject(),
			      this.inject()))
      }
    }
  }
}