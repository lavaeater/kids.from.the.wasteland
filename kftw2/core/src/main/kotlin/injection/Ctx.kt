package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.PerformanceCounters
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.managers.BodyFactory
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.MapManager
import com.lavaeater.kftw.ui.Hud
import ktx.box2d.createWorld
import ktx.inject.Context
import map.TileKeyManager
import map.TileManager

class Ctx {

  companion object {
    val context = Context()
    fun buildContext() {
      context.register {
        bindSingleton(PerformanceCounters())
        bindSingleton(TileKeyManager())
        bindSingleton(TileManager())
        bindSingleton(Player("Thorborg"))
        bindSingleton(SpriteBatch())
        bindSingleton(OrthographicCamera())
        bindSingleton(createWorld())
        bindSingleton(BodyFactory())
        bindSingleton(Engine())
        bindSingleton<IMapManager>(MapManager())
        bindSingleton(ActorFactory())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton(Hud())
        bindSingleton(GameManager())
      }
    }
  }
}