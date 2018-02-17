package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

/**
 * Created by barry on 12/9/15 @ 11:17 PM.
 */
object Assets : Disposable {
    lateinit var am: AssetManager
    val atlases = mapOf(
            "darkdirt" to TextureAtlas(Gdx.files.internal("tiles/darkdirt/darkdirt.txp")),
            "darkgrass" to TextureAtlas(Gdx.files.internal("tiles/darkgrass/darkgrass.txp")),
            "desert" to TextureAtlas(Gdx.files.internal("tiles/desert/desert.txp")),
            "dirt" to TextureAtlas(Gdx.files.internal("tiles/dirt/dirt.txp")),
            "grass" to TextureAtlas(Gdx.files.internal("tiles/grass/grass.txp")),
            "rock" to TextureAtlas(Gdx.files.internal("tiles/rock/rock.txp")),
            "water" to TextureAtlas(Gdx.files.internal("tiles/water/water.txp")))
    val sprites = mutableMapOf<String, HashMap<String, Sprite>>()

    fun load(): AssetManager {
        am = AssetManager()

        var yFactor = 1f
        var xFactor = 1f
        for(atlasMap in atlases) {
            val atlas = atlasMap.value
            sprites.put(atlasMap.key, hashMapOf())
            for (region in atlas.regions) {
                if (region.name != "blank") {
                    val sprite = atlas.createSprite(region.name)
                    sprite.setSize(8f, 8f)
                    sprite.x = xFactor * 8f
                    sprite.y = yFactor * 8f
                    sprites[atlasMap.key]!!.put(region.name, sprite)
                    yFactor++
                }
            }
            xFactor++
            yFactor = 1f
        }

        return am
    }

    override fun dispose() {
        for(atlas in atlases.values)
            atlas.dispose()
    }
}
