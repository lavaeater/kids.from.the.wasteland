package managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.physics.box2d.*
import data.Npc
import data.Player

class CollisionListener(private val messageDispatcher: MessageDispatcher) : ContactListener {

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

    if (evaluatePlayerOnNpcContact(contact)) return

    //one is dynamic, other is static, we are, for now, hitting impassible terrain
    evaluateNpcOnStatic(contact)
  }

  private fun evaluateNpcOnStatic(contact: Contact) {
    val dynamicBody = if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody)
      contact.fixtureA.body else contact.fixtureB.body

    //Check the userData of the body, it's either an NPC; then it needs a message!
    val ud = dynamicBody.userData
    when (ud) {
      is Npc -> messageDispatcher.dispatchMessage(EncounterMessages.CollidedWithImpassibleTerrain, ud)
    }
  }

  private fun evaluatePlayerOnNpcContact(contact: Contact): Boolean {
    if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody &&
        contact.fixtureB.body.type == BodyDef.BodyType.DynamicBody) {
      if (contact.fixtureA.body.userData is Player || contact.fixtureB.body.userData is Player) {
        val npc = if (contact.fixtureA.body.userData is Npc) contact.fixtureA.body.userData as Npc else contact.fixtureB.body.userData as Npc
        messageDispatcher.dispatchMessage(EncounterMessages.PlayerMetSomeone, npc)
      }
      return true
    }
    return false
  }
}