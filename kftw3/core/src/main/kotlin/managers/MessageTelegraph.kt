package managers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import data.Npc
import injection.Ctx
import story.FactsOfTheWorld
import story.StoryManager
import story.fact.Contexts
import story.fact.Facts
import story.places.Place
import story.places.PlacesOfTheWorld


/**
 * This class listens to messages related to... collisions!
 *
 * This is great. There should not be a master / monster / god
 * class that handles all the messages, that seems a bit weird.
 *
 * No, we should have multiple, independet, Telegraphs, that listen
 * to a particular SET of messages and handle them independently.
 *
 * This makes it easire to contain their responsibilities.
 */
class MessageTelegraph (private val factsOfTheWorld: FactsOfTheWorld): Telegraph {

  private val messageDispatcher by lazy { Ctx.context.inject<MessageDispatcher>() }

  private val storyManager by lazy { Ctx.context.inject<StoryManager>() }
  private val placesOfTheWorld by lazy { Ctx.context.inject<PlacesOfTheWorld>() }
  private val gameManager by lazy { Ctx.context.inject<GameManager>() }

  override fun handleMessage(msg: Telegram): Boolean {
    if(msg.message !in Messages.validRange) throw IllegalArgumentException("Message id ${msg.message} not in valid range ${Messages.validRange}")
    when(msg.message) {
      Messages.CollidedWithImpassibleTerrain -> return npcCollidedWithImpassibleTerrain(msg.extraInfo as Npc)
      Messages.PlayerMetSomeone -> return playerEncounteredNpc(msg.extraInfo as Npc) //we send the npc, the player is always available
      Messages.EncounterOver -> encounterOver()
      Messages.FactsUpdated -> checkTheWorld() //this method will trigger all stories to check if their rules have passed, for instance
      Messages.StoryCompleted -> return true //this method will trigger the "story ended" thingie related to a story... ending. Might not be relevant
      Messages.NewTile -> newTile(msg.extraInfo as Pair<Int, Int>)
      Messages.PlayerWentToAPlace -> wentSomewhere(msg.extraInfo as Place)
      Messages.PlayerEnteredANewLocation-> entered(msg.extraInfo as String)
    }
    return true
  }

  private fun entered(locationKey: String) {
    factsOfTheWorld.stateStringFact(Facts.Context, Contexts.InDungeon) //This could be a stack of contexts, that we pop and shit.
    factsOfTheWorld.stateStringFact(Facts.CurrentLocation, locationKey)
    gameManager.updateLocation()
  }

  private fun wentSomewhere(place: Place) {
    factsOfTheWorld.stateStringFact(Facts.CurrentPlace, place.name)
    factsOfTheWorld.stateStringFact(Facts.Context, Contexts.EnteredPlace)
    placesOfTheWorld.enterPlace(place)

  }

  private fun checkTheWorld() {
    /*
    is a place a place or a story
    or a rule or what?

    Is everything connected to a story but the storymanager might
    check our places as well? Or do we need a worldManager?

    We need the good old "global world rules again"
     */
    storyManager.checkStories()
  }

  private fun newTile(newTile: Pair<Int,Int>) {
    /*
    To make it easier, facts are indeed always facts and never shit we read from the code...
     */
    factsOfTheWorld.stateIntFact(Facts.PlayerTileX, newTile.first)
    factsOfTheWorld.stateIntFact(Facts.PlayerTileY, newTile.second)

    /*
    We meed to check the places thing if there is a place in this new tile.
    If there is a place here....

    No, the place thing can be... a rule? No? Yes?

    Is a fucking place a fucking hitbox? FUUUCK

    Places should be entities that have hitboxes. Tiles etc could very well be area-related
    or something
     */


    messageDispatcher.dispatchMessage(Messages.FactsUpdated)
  }

  private fun encounterOver() {
    factsOfTheWorld.clearFacts(setOf(Facts.Context, Facts.CurrentNpc, Facts.CurrentNpcName))
    messageDispatcher.dispatchMessage(Messages.FactsUpdated)
  }

  private fun playerEncounteredNpc(npc: Npc): Boolean {

    /*
    This method shall set some facts.

    Then this method shall send the "facts have been updated"-message
    Game state will be updated by the dialog manager, I believe. - it has a reference to the state manager
     */
    factsOfTheWorld.stateStringFact(Facts.Context, Contexts.MetNpc)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpc, npc.id)
    factsOfTheWorld.stateStringFact(Facts.CurrentNpcName, npc.name)
    messageDispatcher.dispatchMessage(Messages.FactsUpdated)
    return true
  }

  private fun npcCollidedWithImpassibleTerrain(npc: Npc) : Boolean {
    npc.lostInterest()
    return true
  }
}

