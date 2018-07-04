package components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import data.IWorldlyThing
import ktx.math.vec2

class TransformComponent(var position: Vector2 = vec2(0f, 0f), var rotation:Float = 0f): Component


class PositionComponent(private val thing: IWorldlyThing) : Component {
  var position: Vector2
    get () = vec2(thing.worldX, thing.worldY)
    set(value) {
      thing.worldX = value.x
      thing.worldY = value.y
    }
}