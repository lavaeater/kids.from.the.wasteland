package com.lavaeater.kftw.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.data.Npc
import com.lavaeater.kftw.data.Player
import com.lavaeater.kftw.injection.Ctx
import ktx.ashley.mapperFor
import javax.xml.crypto.dsig.Transform

/*
This task shall contain it's own stuff, and just direct the npc somewhere, the
npc shouldn't have to have a million specific methods, we should use the
framework around to get stuff done.

In this case, the npc should have some kind of food preference, a "type", sort of like
will it eat anything or what?
 */

/*

All roaming creatures should be driven by needs. Animals that are not warmblooded
can lay dormant for weeks (snakes) waiting to spring a trap on a victim. Others can walk extremely
long distances to get food (wolves).

Others must use their intelligence to gather food. Now, we don't need to make a complex framework
for food or energy, but getting food / loot can be done in different ways and different animals
should be able to employ different strategies, somehow.

The tasks should be generic, usable for any type of NPC.

They might employ different strategies (see strategy pattern) for accomplishing...

 */

/*
This task can be used for an npc to periodically check if there is a target nearby -
but what is a target? For now, the only interesting target is the PLAYER
 */
class CheckForPlayer : LeafTask<Npc>() {
  val player = Ctx.context.inject<Player>()
  val transMpr = mapperFor<TransformComponent>()
  override fun execute(): Status {
    val npc = `object`



    if(npc.scavenge()) {
      return Task.Status.SUCCEEDED
    }

//    return Task.Status.RUNNING
  }

  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }
}