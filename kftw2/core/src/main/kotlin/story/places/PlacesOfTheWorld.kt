package story.places

import com.badlogic.gdx.math.MathUtils
import data.Player
import factory.ActorFactory
import injection.Ctx
import managers.GameEvents
import managers.GameState
import map.IMapManager
import story.StoryHelper.Companion.factsOfTheWorld
import story.conversation.ConversationManager
import story.conversation.InlineConvo
import story.fact.Facts

class PlacesOfTheWorld {

  val player by lazy { Ctx.context.inject<Player>() }
  val gameState by lazy { Ctx.context.inject<GameState>() }
  val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  val mapManager by lazy { Ctx.context.inject<IMapManager>() }
  val actorFactory by lazy { Ctx.context.inject<ActorFactory>() }

  init {
    val someTilesInRange = mapManager.getBandOfTiles(player.currentX, player.currentY,
        10, 5).filter {
      it.tile.tileType != "rock" && it.tile.tileType != "water"
    }.toMutableList()
    for(city in 0..5) {

      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
      someTilesInRange.remove(randomlySelectedTile)

      val actor = actorFactory.addFeatureEntity("city_$city", randomlySelectedTile.x, randomlySelectedTile.y)
    }
  }

  fun enterPlace(place:Place) {
    /*
    show some shit for a city. For now, how about we show a little conversation?
     */
    gameState.handleEvent(GameEvents.DialogStarted)
    conversationManager.startConversation(
        createPlaceConvo(),
        {
          //set some facts?
          var bla = "Blo"
        },
        true,
        false)

  }

  private fun createPlaceConvo() :InlineConvo {

    val antagonistLines = mutableMapOf<Int, List<String>>()

    antagonistLines[0] = listOf(
            "Välkommen till staden ${factsOfTheWorld.getStringFact(Facts.CurrentPlace).value}!",
            "Ödemarkens sanna pärla!",
            "Vill du hedra oss med ett besök?"
        )
    antagonistLines[1] = listOf(
        "Än så länge kan man inget göra i städer!"
    )
    return InlineConvo(player, antagonistLines = antagonistLines)
   }
}