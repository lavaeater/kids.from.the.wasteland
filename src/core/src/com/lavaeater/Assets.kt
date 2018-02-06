package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.managers.WorldManager

/**
 * Created by barry on 12/9/15 @ 11:17 PM.
 */
object Assets : Disposable {
    lateinit var am: AssetManager
    val textureAtlas: TextureAtlas by lazy { TextureAtlas("pes/textures.txt") }
    val darkDirtAtlas: TextureAtlas by lazy {TextureAtlas("tiles/darkdirt/darkdirt.txt")}
    val sprites = HashMap<String, Sprite>()
    val darkDirtSprites = HashMap<String, Sprite>()

    val pewSound: Sound by lazy { Gdx.audio.newSound(Gdx.files.internal("sound/pew.ogg"))}
    fun load(): AssetManager {
        am = AssetManager()
        for (region in Assets.textureAtlas.regions) {
            val sprite = Assets.textureAtlas.createSprite(region.name)
            if(region.name == "missile01")
                sprite.setSize(sprite.width * WorldManager.SCALE * 0.7f, sprite.height * WorldManager.SCALE * 0.7f) //Map items are 4 times the size
            else if(region.name != "ship")
                sprite.setSize(sprite.width * WorldManager.SCALE * 4f, sprite.height * WorldManager.SCALE * 4f) //Map items are 4 times the size
            else
                sprite.setSize(sprite.width * WorldManager.SCALE, sprite.height * WorldManager.SCALE)

            sprites.put(region.name, sprite)
        }

        for(region in Assets.darkDirtAtlas.regions) {
            sprites.put(region.name, Assets.darkDirtAtlas.createSprite(region.name))
        }
        return am
    }

    override fun dispose() {
        textureAtlas.dispose()
        pewSound.dispose()
    }
}
