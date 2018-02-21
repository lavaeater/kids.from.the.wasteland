package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3

interface IMapManager {
    fun getVisibleTiles(position: Vector3) : Map<TileKey, Tile>
    fun getTileForPosition(position: Vector3): Tile
}