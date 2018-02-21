package com.lavaeater.kftw.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.components.Npc

class Scavenge : LeafTask<Npc>() {
    override fun execute(): Status {
      val npc = `object`
      if(npc.scavenge()) {
        return Task.Status.SUCCEEDED
      }

      return Task.Status.FAILED
    }

    override fun copyTo(task: Task<Npc>?): Task<Npc> {
      return task!!
    }
}

class LostInterest : LeafTask<Npc>() {
  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }

  val probability = 0.5f
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

class Wander : LeafTask<Npc>() {
  override fun execute(): Status {
    val npc = `object`
    npc.wander()
    return Status.FAILED
  }

  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }

}

class WalkTo : LeafTask<Npc>() {
  override fun execute(): Status {
    val npc = `object`
    if(npc.walkTo())
      return Status.SUCCEEDED
    return Status.FAILED
  }

  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }

}

class FindTile: LeafTask<Npc>() {
  override fun execute(): Status {
    val npc = `object`
    if(npc.findTile())
      return Task.Status.SUCCEEDED
    return Task.Status.FAILED
  }

  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }
}