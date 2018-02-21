package com.lavaeater.kftw.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import com.lavaeater.kftw.components.Npc
import com.lavaeater.kftw.components.Dog
import com.lavaeater.kftw.components.Dog
import com.lavaeater.kftw.components.Dog
import com.lavaeater.kftw.components.Dog









/** @author implicit-invocation
 * @author davebaol
 */
class BarkTask : LeafTask<Npc>() {

    @TaskAttribute
    var times: IntegerDistribution = ConstantIntegerDistribution.ONE

    private var t: Int = 0

    override fun start() {
        super.start()
        t = times.nextInt()
    }

    override fun execute(): Task.Status {
        val dog = `object`
        for (i in 0 until t)
            dog.bark()
        return Task.Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        val bark = task as BarkTask
        bark.times = times

        return task
    }

    override fun reset() {
        times = ConstantIntegerDistribution.ONE
        t = 0
        super.reset()
    }
}

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation .TaskAttribute

/** @author implicit-invocation
 * @author davebaol
 */
class CareTask : LeafTask<Dog>() {

    @TaskAttribute(required = true)
    var urgentProb = 0.8f

    override fun execute(): Task.Status {
        if (Math.random() < urgentProb) {
            return Task.Status.SUCCEEDED
        }
        val dog = `object`
        dog.brainLog("GASP - Something urgent :/")
        return Task.Status.FAILED
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        val care = task as CareTask
        care.urgentProb = urgentProb

        return task
    }

    override fun reset() {
        urgentProb = 0.8f
        super.reset()
    }

}

class MarkTask : LeafTask<Dog>() {

    internal var i: Int = 0

    override fun start() {
        i = 0
        `object`.log("Dog lifts a leg and pee!")
    }

    override fun execute(): Task.Status {
        val dog = `object`
        val result = dog.markATree(i++) ?: return Task.Status.RUNNING
        return if (result) Task.Status.SUCCEEDED else Task.Status.FAILED
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        return task
    }

    override fun reset() {
        i = 0
        super.reset()
    }
}

class PlayTask : LeafTask<Dog>() {

    override fun start() {
        val dog = `object`
        dog.brainLog("WOW - Lets play!")
    }

    override fun execute(): Task.Status {
        val dog = `object`
        dog.brainLog("PANT PANT - So fun")
        return Task.Status.RUNNING
    }

    override fun end() {
        val dog = `object`
        dog.brainLog("SIC - No time to play :(")
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        return task
    }
}

class RestTask : LeafTask<Dog>() {

    override fun start() {
        `object`.brainLog("YAWN - So tired...")
    }

    override fun execute(): Task.Status {
        `object`.brainLog("zz zz zz")
        return Task.Status.RUNNING
    }

    override fun end() {
        `object`.brainLog("SOB - Time to wake up")
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        return task
    }

}

class WalkTask : LeafTask<Dog>() {

    private var i = 0

    override fun start() {
        i = 0
        val dog = `object`
        dog.startWalking()
    }

    override fun execute(): Task.Status {
        i++
        val dog = `object`
        dog.randomlyWalk()
        return if (i < 3) Task.Status.RUNNING else Task.Status.SUCCEEDED
    }

    override fun end() {
        `object`.stopWalking()
    }

    override fun copyTo(task: Task<Dog>): Task<Dog> {
        return task
    }

    override fun reset() {
        i = 0
        super.reset()
    }

}