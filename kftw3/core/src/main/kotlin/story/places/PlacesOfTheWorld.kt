package story.places

import com.badlogic.gdx.math.MathUtils
import data.Player
import factory.ActorFactory
import injection.Ctx
import managers.GameEvents
import managers.GameState
import map.IMapManager
import story.FactsOfTheWorld
import story.conversation.ConversationManager
import story.conversation.InlineConvo
import story.conversation.InternalConversation
import story.convo
import story.fact.Facts

class PlacesOfTheWorld {

  val player by lazy { Ctx.context.inject<Player>() }
  val gameState by lazy { Ctx.context.inject<GameState>() }
  val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  val mapManager by lazy { Ctx.context.inject<IMapManager>() }
  val actorFactory by lazy { Ctx.context.inject<ActorFactory>() }
  val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

  init {
    val someTilesInRange = mapManager.getBandOfTiles(player.currentX, player.currentY,
        20, 7).filter {
      it.tile.tileType != "rock" && it.tile.tileType != "water"
    }.toMutableList()
    for(city in 0..10) {

      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
      someTilesInRange.remove(randomlySelectedTile)
      val tilesInRangeOfSelected = mapManager.getTilesInRange(randomlySelectedTile.x, randomlySelectedTile.y, 5)
      //Remove a lot of tiles from the band of possible tiles to have the city at
      for(tile in tilesInRangeOfSelected)
        someTilesInRange.remove(tile)

      actorFactory.addFeatureEntity("city_$city", randomlySelectedTile.x, randomlySelectedTile.y)
    }
  }

  fun enterPlace(place:Place) {
    /*
    show some shit for a city. For now, how about we show a little conversation?
     */
    gameState.handleEvent(GameEvents.DialogStarted)
    conversationManager.startConversation(
        anotherConvo(),
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

  private fun anotherConvo() : InternalConversation {
    return convo {
      startingStepKey = "start"
      step {
        key = "start"
        addLine("Välkommen!")
        addLine("Du är säkert trött sedan resan")
        addLine("- kom in och ta ett glas")
        positive("entered_house", "Ja tack, gärna, jag är otroligt törstig")
        abort("abort", "Nej tack, så törstig är jag inte.")
        rude("abort", "Nej tack, så du är jag inte att jag dricker ditt vatten.")
      }
      step {
        key = "entered_house"
        addLine("Du har säkert rest länge och väl.")
        addLine("Ödemarken är inte snäll mot en vandrares fötter.")
        addLine("Här, drick vatten!")
        positive("is_poisoned", "Ja, gud så törstig jag är!")
        abort("abort", "Nej, vid närmare eftertanke kom jag nog på att jag måste gå nu!")
      }
      step {
        key = "is_poisoned"
        addLine("Ha! HA! HAHA!")
        addLine("Det var inte vatten, din idiot")
        addLine("Det är gift!")
        addLine("Men du dör inte. Oroa dig inte.")
        addLine("Nej, du kommer bli slö. trött och dum.")
        addLine("Du kommer lyda allt jag säger. Väldigt bra...")
        addLine("...för en slav i gruvan")
        rude("abort", "FAN ta dig!")
      }
    }
  }
}