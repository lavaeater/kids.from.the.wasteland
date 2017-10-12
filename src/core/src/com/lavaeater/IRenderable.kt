package com.lavaeater

import com.badlogic.gdx.graphics.g2d.Batch

/**
 * Created by 78899 on 2017-10-09.
 */
interface IRenderable<T> where T:Batch {
    fun render(batch: T)
}

