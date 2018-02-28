package com.lavaeater.kftw.managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.physics.box2d.*
import com.lavaeater.kftw.components.Npc
import com.lavaeater.kftw.injection.Ctx

class CollisionMessageManager() : ContactListener {
  val messageDispatcher = Ctx.context.inject<MessageDispatcher>()

  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

  }

  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
  }

  override fun endContact(contact: Contact?) {

  }

  override fun beginContact(contact: Contact) {

    //If both bodies are static, we're not interested, just return
    if (contact.fixtureA.body.type == BodyDef.BodyType.StaticBody &&
        contact.fixtureB.body.type == BodyDef.BodyType.StaticBody) return

    //If both are dynamic, we do something for that, later
    if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody &&
        contact.fixtureB.body.type == BodyDef.BodyType.DynamicBody) return

    //one is dynamic, other is static, we are, for now, hitting impassible terrain
    val dynamicBody = if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody)
      contact.fixtureA.body else contact.fixtureB.body

    //Check the userData of the body, it's either an NPC; then it needs a message!
    val ud = dynamicBody.userData
    when(ud) {
      is Npc -> sendMessageToNpc(ud)
    }
  }

  private fun sendMessageToNpc(npc: Npc) {
    messageDispatcher.dispatchMessage(Messages.CollidedWithImpassibleTerrain, npc)
  }

  init {
  }
}