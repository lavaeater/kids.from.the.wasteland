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
            "V??lkommen till staden ${factsOfTheWorld.getStringFact(Facts.CurrentPlace).value}!",
            "??demarkens sanna p??rla!",
            "Vill du hedra oss med ett bes??k?"
        )
    antagonistLines[1] = listOf(
        "??n s?? l??nge kan man inget g??ra i st??der!"
    )
    return InlineConvo(player, antagonistLines = antagonistLines)
   }

  private fun anotherConvo() : InternalConversation {
    return convo {
      startingStepKey = "start"
      step {
        key = "start"
        addLine("V??lkommen!")
        addLine("Du ??r s??kert tr??tt sedan resan")
        addLine("- kom in och ta ett glas")
        positive("entered_house", "Ja tack, g??rna, jag ??r otroligt t??rstig")
        abort("abort", "Nej tack, s?? t??rstig ??r jag inte.")
        rude("abort", "Nej tack, s?? du ??r jag inte att jag dricker ditt vatten.")
      }
      step {
        key = "entered_house"
        addLine("Du har s??kert rest l??nge och v??l.")
        addLine("??demarken ??r inte sn??ll mot en vandrares f??tter.")
        addLine("H??r, drick vatten!")
        positive("is_poisoned", "Ja, gud s?? t??rstig jag ??r!")
        abort("abort", "Nej, vid n??rmare eftertanke kom jag nog p?? att jag m??ste g?? nu!")
      }
      step {
        key = "is_poisoned"
        addLine("Ha! HA! HAHA!")
        addLine("Det var inte vatten, din idiot")
        addLine("Det ??r gift!")
        addLine("Men du d??r inte. Oroa dig inte.")
        addLine("Nej, du kommer bli sl??. tr??tt och dum.")
        addLine("Du kommer lyda allt jag s??ger. V??ldigt bra...")
        addLine("...f??r en slav i gruvan")
        rude("abort", "FAN ta dig!")
      }
    }
  }
}