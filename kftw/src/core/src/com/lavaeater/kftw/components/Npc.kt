package com.lavaeater.kftw.components

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.ai.btree.BehaviorTree



class Npc(val npcType: NpcType, var strength: Int = npcType.strength, var health: Int = npcType.health, var speed: Int = npcType.speed, var attack: Int = npcType.attack, var attackString: String = npcType.attackString) {

}

class Dog @JvmOverloads constructor(var name: String, var behaviorTree: BehaviorTree<Dog>? = null) {
    var brainLog: String

    init {
        this.brainLog = name + " brain"
        if (behaviorTree != null) behaviorTree!!.`object` = this
    }

    fun bark() {
        if (MathUtils.randomBoolean())
            log("Arf arf")
        else
            log("Woof")
    }

    fun startWalking() {
        log("Let's find a nice tree")
    }

    fun randomlyWalk() {
        log("SNIFF SNIFF - Dog walks randomly around!")
    }

    fun stopWalking() {
        log("This tree smells good :)")
    }

    fun markATree(i: Int): Boolean? {
        if (i == 0) {
            log("Swoosh....")
            return null
        }
        if (MathUtils.randomBoolean()) {
            log("MUMBLE MUMBLE - Still leaking out")
            return java.lang.Boolean.FALSE
        }
        log("I'm ok now :)")
        return java.lang.Boolean.TRUE
    }

    //	private boolean urgent = false;
    //
    //	public boolean isUrgent () {
    //		return urgent;
    //	}
    //
    //	public void setUrgent (boolean urgent) {
    //		this.urgent = urgent;
    //	}

    fun log(msg: String) {
        GdxAI.getLogger().info(name, msg)
    }

    fun brainLog(msg: String) {
        GdxAI.getLogger().info(brainLog, msg)
    }

}