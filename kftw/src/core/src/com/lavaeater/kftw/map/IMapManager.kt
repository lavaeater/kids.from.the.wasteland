package com.lavaeater.kftw.map

import com.badlogic.gdx.math.Vector3

interface IMapManager {
    fun getVisibleTiles(position: Vector3) : List<Tile>
}