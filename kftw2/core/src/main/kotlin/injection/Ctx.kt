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
        bindSingleton<InputProcessor>(InputMultiplexer())
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
        bindSingleton(GameState())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton<IUserInterface>(UserInterface())
        bindSingleton(ConversationManager())
        bindSingleton(MessageManager())
        bindSingleton(GameManager(gameSettings))
      }
    }
  }
}