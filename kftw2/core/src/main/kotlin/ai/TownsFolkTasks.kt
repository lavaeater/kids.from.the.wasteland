package com.lavaeater.kftw.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.MathUtils
import data.Creature

class Scavenge : LeafTask<Creature>() {
    override fun execute(): Status {
      val npc = `object`
      if(npc.scavenge()) {
        return Task.Status.SUCCEEDED
      }

      return Task.Status.FAILED
    }

    override fun copyTo(task: Task<Creature>?): Task<Creature> {
      return task!!
    }
}

class LostInterest : LeafTask<Creature>() {
  override fun copyTo(task: Task<Creature>?): Task<Creature> {
    return task!!
  }

  val probability = 0.25f
  override fun execute(): Status {
    if(MathUtils.random() < probability) {
      //Change desired tile type to something other than we have now!
      val npc = `object`
      npc.lostInterest()
      return Task.Status.FAILED
    }
    return Task.Status.SUCCEEDED
  }

}

class Wander : LeafTask<Creature>() {
  override fun execute(): Status {
    val npc = `object`
    if(npc.wander())
        return Status.SUCCEEDED //As long as the agent is wandering, we keep wandering. The npccontrol will find a tile and when there, will change to idle and then we fail!
    return Status.FAILED
  }

  override fun copyTo(task: Task<Creature>?): Task<Creature> {
    return task!!
  }

}

class WalkTo : LeafTask<Creature>() {
  override fun execute(): Status {
    val npc = `object`
    if(npc.walkTo())
      return Status.SUCCEEDED
    return Status.FAILED
  }

  override fun copyTo(task: Task<Creature>?): Task<Creature> {
    return task!!
  }

}

class FindTile: LeafTask<Creature>() {
  override fun execute(): Status {
    val npc = `object`
    if(npc.findTile())
      return Task.Status.SUCCEEDED
    return Task.Status.FAILED
  }

  override fun copyTo(task: Task<Creature>?): Task<Creature> {
    return task!!
  }
}