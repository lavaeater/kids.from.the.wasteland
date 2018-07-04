package managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.physics.box2d.*
import data.Creature
import data.Player
import story.places.Place

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
    evaluateDynamicOnStatic(contact)
  }

  private fun evaluateDynamicOnStatic(contact: Contact) {
    /*
    Either player on static bodies OR a @Place

     */
    val dynamicBody = if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody)
      contact.fixtureA.body else contact.fixtureB.body

    //Check the userData of the body, it's either an NPC; then it needs a message!
    val ud = dynamicBody.userData
    when (ud) {
      is Player -> evaluatePlayerOnStaticContact(ud, contact)
      is Creature -> messageDispatcher.dispatchMessage(Messages.CollidedWithImpassibleTerrain, ud)
    }
  }

  private fun evaluatePlayerOnStaticContact(player: Player, contact: Contact) {
    val staticBody = if (contact.fixtureA.body.type == BodyDef.BodyType.StaticBody)
      contact.fixtureA.body else contact.fixtureB.body

    val ud = staticBody.userData
    when(ud) {
      is Place -> messageDispatcher.dispatchMessage(Messages.PlayerWentToAPlace, ud)
    }
  }

  private fun evaluatePlayerOnNpcContact(contact: Contact): Boolean {
    if (contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody &&
        contact.fixtureB.body.type == BodyDef.BodyType.DynamicBody) {
      if (contact.fixtureA.body.userData is Player || contact.fixtureB.body.userData is Player) {
        val npc = if (contact.fixtureA.body.userData is Creature) contact.fixtureA.body.userData as Creature else contact.fixtureB.body.userData as Creature
        messageDispatcher.dispatchMessage(Messages.PlayerMetSomeone, npc)
      }
      return true
    }
    return false
  }
}