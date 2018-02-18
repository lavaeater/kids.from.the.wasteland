package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3
import com.lavaeater.kftw.managers.WorldManager
import com.lavaeater.kftw.systems.toTile

class AreaMapManager : MapManagerBase() {

    override fun getVisibleTiles(position: Vector3): List<Tile> {
        if(doWeNeedNewVisibleTiles(position)) {
            visibleTiles.clear()
            currentKey = position.toTile(WorldManager.TILE_SIZE)
            //Our map is actually static. We can just filter the structure on the keys!

            visibleTiles.addAll(mapStructure.filterKeys { pair -> pair.isInRange(currentKey, widthInTiles) }.map { it.value })
        }
        return visibleTiles
    }
}