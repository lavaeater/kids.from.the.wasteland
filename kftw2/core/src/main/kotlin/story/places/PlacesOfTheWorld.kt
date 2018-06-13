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
import story.conversation.InternalConversation
import story.convo

class PlacesOfTheWorld {

  val player by lazy { Ctx.context.inject<Player>() }
  private val gameState by lazy { Ctx.context.inject<GameState>() }
  private val conversationManager by lazy { Ctx.context.inject<ConversationManager>() }
  private val mapManager by lazy { Ctx.context.inject<IMapManager>() }
  private val actorFactory by lazy { Ctx.context.inject<ActorFactory>() }
  val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }
	val cityNames = arrayOf(
			"Bytarstan",
			"Oljestan",
			"Slavstan",
			"Hålegrund",
			"Snygelbro",
			"Mygelhamn",
			"Wonkelbo",
			"Sandhamn",
			"Bylte",
			"Hylte"
			)

  init {
//    val someTilesInRange = mapManager.getBandOfTiles(player.currentX, player.currentY,
//        20, 7).filter {
//      it.tile.tileType != "rock" && it.tile.tileType != "water"
//    }.toMutableList()
//    for(city in 0..9) {
//
//      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
//      someTilesInRange.remove(randomlySelectedTile)
//      val tilesInRangeOfSelected = mapManager.getTilesInRange(randomlySelectedTile.x, randomlySelectedTile.y, 5)
//      //Remove a lot of tiles from the band of possible tiles to have the city at
//      for(tile in tilesInRangeOfSelected)
//        someTilesInRange.remove(tile)
//
//      actorFactory.addFeatureEntity(cityNames[city], randomlySelectedTile.x, randomlySelectedTile.y)
//    }
  }

  fun enterPlace(place:Place) {
    /*
    show some shit for a city. For now, how about we show a little conversation?
     */
    gameState.handleEvent(GameEvents.DialogStarted)
    conversationManager.startConversation(
        placeConvo(place),
        {},
        true,
        false)
  }

	private fun placeConvo(place:Place) : InternalConversation {
		val city_gate = "Återvänd till stadsporten"
		val topKey = "enter_city"
		return convo {
			step {
				key = "start"
				addLine("Du har anlänt till ${place.name}")
				addLine("Vad vill du göra nu?")
				positive(topKey, "Jag vill besöka staden")
				negative("abort", "Jag fortsätta min resa")
			}
			step {
				key = topKey
				addLine("Allt luktar illa i den här staden")
				addLine("Men det finns mat att köpa,")
				addLine("handlare att besöka,")
				addLine("och en anslagstavla med anslag.")
				addLine("Vad vill du göra?")
				neutral("food", "Jag vill äta")
				neutral("trade", "Jag vill handla")
				neutral("bulletin", "Jag vill läsa anslagen")
				negative("start", "Jag vill gå ut ur staden")
			}
			bulletin(topKey)
			trader(topKey)
			step {
				key ="food"
				addLine("Som alla städer i regionen")
				addLine("säljs maten på ett stökigt torg.")
				addLine("säljs maten på ett stökigt torg.")
				addLine("Lokala specialiteter blandas med exotisk mat.")
				neutral("eat_food", "Köp kebab")
				neutral("eat_food", "Köp djur-på-pinne")
				neutral("eat_food", "Köp inte-alls-människokött")
				negative(topKey, city_gate)
			}
			step {
				key = "eat_food"
				addLine("Maten är förvånansvärt god")
				addLine("och mättande.")
				addLine("Men dess verkliga konsekvenser brukar")
				addLine("ta ett par dagar.")
				negative(topKey, city_gate)
			}
		}
	}

  private fun internalConversation() : InternalConversation {
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