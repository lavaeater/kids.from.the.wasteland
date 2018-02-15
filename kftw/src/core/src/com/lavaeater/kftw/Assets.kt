package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

/**
 * Created by barry on 12/9/15 @ 11:17 PM.
 */
object Assets : Disposable {
    lateinit var am: AssetManager
    val atlases = mapOf("dirt" to TextureAtlas("tiles/dirt/dirt.txp"))
    val sprites = mutableMapOf<String, HashMap<String, Sprite>>()

    fun load(): AssetManager {
        am = AssetManager()

        var i = 1f
        for(atlasMap in atlases) {
            val atlas = atlasMap.value
            sprites.put(atlasMap.key, hashMapOf())
            for (region in atlas.regions) {
                if (region.name != "blank") {
                    val sprite = atlas.createSprite(region.name)
                    sprite.setSize(64f, 64f)
                    sprite.x = i * 64 + 300f
                    sprite.y = i * 64 + 300f
                    sprites[atlasMap.key]!!.put(region.name, sprite)
                    i++
                }
            }
        }

        return am
    }

    override fun dispose() {
        for(atlas in atlases.values)
            atlas.dispose()
    }
}
