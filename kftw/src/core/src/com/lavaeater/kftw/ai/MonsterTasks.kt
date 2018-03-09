package com.lavaeater.kftw.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.lavaeater.kftw.data.Npc

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

 */

class SearchForFood : LeafTask<Npc>() {
  override fun execute(): Status {
    val npc = `object`

    /*
    So what do we do?

    
     */

    if(npc.scavenge()) {
      return Task.Status.SUCCEEDED
    }

    return Task.Status.RUNNING
  }

  override fun copyTo(task: Task<Npc>?): Task<Npc> {
    return task!!
  }
}