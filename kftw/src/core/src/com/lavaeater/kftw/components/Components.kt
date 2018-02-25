package com.lavaeater.kftw.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ktx.math.vec2
import java.util.*

class TransformComponent(var position: Vector2 = vec2(0f,0f), var rotation:Float = 0f): Component
class WorldMapComponent : Component
class AiComponent<T>(val behaviorTree: BehaviorTree<T>) : Component
class CharacterSpriteComponent(val spriteKey: String) : Component
class AnimatedCharacterSpriteComponent(val spriteKey: String) : Component
class NpcComponent(val npc: Npc):Component
class CameraFollowComponent : Component
class KeyboardControlComponent(val id : UUID = UUID.randomUUID()): Component
class Box2dBodyComponent(val body: Body) : Component

data class NpcType(val strength:Int, val health: Int, val speed: Int, val attack: Int, val attackString: String)
