package data

import com.badlogic.gdx.ai.msg.MessageDispatcher
import injection.Ctx
import managers.Messages
import kotlin.properties.Delegates


class Player(override val id:String = "Player",
             override var name:String,
             override var strength: Int = 10,
             override var health: Int = 10,
             override var intelligence: Int = 10,
             override var sightRange: Int = 3) : IAgent {
  override val inventory = mutableListOf("Mat", "Extra varm rock", "Litet, dåligt svärd")
  override val skills: MutableMap<String, Int> =
      mutableMapOf(
          "tracking" to 50,
          "stealth" to 35)

  val messageDispatcher by lazy { Ctx.context.inject<MessageDispatcher>()}

  var canSend = true

  override var currentX:Int by Delegates.observable(0, { _, oldValue, newValue -> if(oldValue != newValue && canSend) {
    canSend = false //might stop tons of sending...
    messageDispatcher.dispatchMessage(Messages.NewTile, Pair(currentX, currentY))
    canSend = true
  }
  })
  override var currentY:Int by Delegates.observable(0, { _, oldValue, newValue -> if(oldValue != newValue && canSend) {
    canSend = false //might stop tons of sending...
    messageDispatcher.dispatchMessage(Messages.NewTile, Pair(currentX, currentY))
    canSend = true
  }
  })
}

