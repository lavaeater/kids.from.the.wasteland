package com.lavaeater.kftw.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.BehaviorTree

class TransformComponent(var x:Float=0f, var y:Float = 0f, var rotation:Float = 0f): Component
class WorldMapComponent : Component
class AiComponent(var npc: Npc, val behaviorTree: BehaviorTree<Npc>) : Component
class CharacterSpriteComponent(val spriteKey: String) : Component

data class NpcType(val strength:Int, val health: Int, val speed: Int, val attack: Int, val attackString: String)
