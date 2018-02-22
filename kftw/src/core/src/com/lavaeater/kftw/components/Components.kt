package com.lavaeater.kftw.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.BehaviorTree

class TransformComponent(var x:Float=0f, var y:Float = 0f, var rotation:Float = 0f): Component
class WorldMapComponent : Component
class AiComponent<T>(val behaviorTree: BehaviorTree<T>) : Component
class CharacterSpriteComponent(val spriteKey: String) : Component
class NpcComponent(val npc: Npc):Component

data class NpcType(val strength:Int, val health: Int, val speed: Int, val attack: Int, val attackString: String)
