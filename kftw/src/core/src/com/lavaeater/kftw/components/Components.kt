package com.lavaeater.kftw.components

import com.badlogic.ashley.core.Component

class TransformComponent(var x:Float=0f, var y:Float = 0f, var rotation:Float = 0f): Component
class WorldMapComponent : Component
class NpcComponent(var npc: Npc) : Component
class CharacterSpriteComponent(val spriteKey: String) : Component

data class Npc(val npcType: NpcType, var strength: Int = npcType.strength, var health: Int = npcType.health, var speed: Int = npcType.speed, var attack: Int = npcType.attack, var attackString: String = npcType.attackString)
data class NpcType(val strength:Int, val health: Int, val speed: Int, val attack: Int, val attackString: String)
