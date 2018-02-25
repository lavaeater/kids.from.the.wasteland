package com.lavaeater.kftw.managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.physics.box2d.*

class CollisionMessageManager(messageDispatcher: MessageDispatcher) : ContactListener{
  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

  }

  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
  }

  override fun endContact(contact: Contact?) {

  }

  override fun beginContact(contact: Contact?) {
    
  }

  init {
  }
}