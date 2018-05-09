package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.PerformanceCounters
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.managers.BodyFactory
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.MapManager
import com.lavaeater.kftw.ui.UserInterface
import com.lavaeater.kftw.ui.IUserInterface
import ktx.box2d.createWorld
import ktx.inject.Context
import managers.MessageManager
import map.TileManager
import story.ConversationManager

class Ctx {

  companion object {
    val context = Context()
    fun buildContext() {
      context.register {
        bindSingleton(PerformanceCounters())
        bindSingleton(TileManager())
        bindSingleton(Player("Thorborg"))
        bindSingleton<Batch>(SpriteBatch())
        bindSingleton<Camera>(OrthographicCamera())
        bindSingleton(createWorld())
        bindSingleton(BodyFactory())
        bindSingleton(Engine())
        bindSingleton<IMapManager>(MapManager())
        bindSingleton(ActorFactory())
        bindSingleton(GameStateManager())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton<IUserInterface>(UserInterface())
        bindSingleton(ConversationManager())
        bindSingleton(MessageManager())
        bindSingleton(GameManager())
      }
    }
  }
}