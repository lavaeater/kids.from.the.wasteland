package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.kftw.managers.ActorManager
import com.lavaeater.kftw.managers.BodyManager
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.managers.GameStateManager
import com.lavaeater.kftw.map.AreaMapManager
import com.lavaeater.kftw.map.IMapManager
import ktx.box2d.createWorld
import ktx.inject.Context

class Ctx {

  companion object {
    val context = Context()
    fun buildContext() {
      context.register {
        bindSingleton(SpriteBatch())
        bindSingleton(OrthographicCamera())
        bindSingleton(createWorld())
        bindSingleton(BodyManager())
        bindSingleton(Engine())
        bindSingleton<IMapManager>(AreaMapManager())
        bindSingleton(ActorManager())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton(GameManager())
      }
    }
  }
}