package data

import com.badlogic.gdx.ai.msg.MessageDispatcher
import injection.Ctx
import managers.Messages
import story.FactsOfTheWorld
import story.fact.Facts
import kotlin.properties.Delegates


class Player(override val id:String = "Player",
             override var name:String,
             override var strength: Int = 10,
             override var health: Int = 10,
             override var intelligence: Int = 10,
             override var sightRange: Int = 8) : IAgent {
  override val inventory = mutableListOf("Mat", "Extra varm rock", "Litet, dåligt svärd")
  override val skills: MutableMap<String, Int> =
      mutableMapOf(
          "tracking" to 50,
          "stealth" to 35)

  private val messageDispatcher by lazy { Ctx.context.inject<MessageDispatcher>()}

  val initialX by lazy { Ctx.context.inject<FactsOfTheWorld>().getIntValue(Facts.PlayerTileX)}
  val initialY by lazy { Ctx.context.inject<FactsOfTheWorld>().getIntValue(Facts.PlayerTileY)}

  var canSend = true

  override var tileX:Int by Delegates.observable(initialX, { _, oldValue, newValue -> if(oldValue != newValue && canSend) {
    canSend = false //might stop tons of sending...
    messageDispatcher.dispatchMessage(Messages.NewTile, Pair(tileX, tileY))
    canSend = true
  }
  })
  override var tileY:Int by Delegates.observable(initialY, { _, oldValue, newValue -> if(oldValue != newValue && canSend) {
    canSend = false //might stop tons of sending...
    messageDispatcher.dispatchMessage(Messages.NewTile, Pair(tileX, tileY))
    canSend = true
  }
  })
}

