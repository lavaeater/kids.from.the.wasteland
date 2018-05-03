package com.lavaeater.kftw.injection

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.managers.ActorFactory
import com.lavaeater.kftw.managers.BodyFactory
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.AreaMapManager
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.ui.Hud
import ktx.box2d.createWorld
import ktx.inject.Context
import map.TileKeyStore

class Ctx {

  companion object {
    val context = Context()
    fun buildContext() {
      context.register {
        bindSingleton(TileKeyStore(-500000, 500000))
        bindSingleton(Player("Thorborg"))
        bindSingleton(SpriteBatch())
        bindSingleton(OrthographicCamera())
        bindSingleton(createWorld())
        bindSingleton(BodyFactory())
        bindSingleton(Engine())
        bindSingleton<IMapManager>(AreaMapManager())
        bindSingleton(ActorFactory())
        bindSingleton<MessageDispatcher>(com.badlogic.gdx.ai.msg.MessageManager.getInstance())
        bindSingleton(Hud())
        bindSingleton(GameManager())
      }
    }
  }
}