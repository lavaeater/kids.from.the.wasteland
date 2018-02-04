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
    lateinit var darkDirtAtlas: TextureAtlas
    val sprites = HashMap<String, Sprite>()
    val darkDirtSprites = HashMap<String, Sprite>()

    val pewSound: Sound by lazy { Gdx.audio.newSound(Gdx.files.internal("sound/pew.ogg"))}
    fun load(): AssetManager {
        am = AssetManager()

        Assets.darkDirtAtlas = TextureAtlas("tiles/darkdirt/darkdirt.txt")



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

        var i = 1f

        for(region in Assets.darkDirtAtlas.regions) {
            val sprite = Assets.darkDirtAtlas.createSprite(region.name)
            sprite.setSize(64f,64f)
            sprite.x = i * 64 + 300f
            sprite.y = i * 64 + 300f

            Assets.darkDirtSprites.put(region.name, sprite)
            i++
        }
        return am
    }

    override fun dispose() {
        textureAtlas.dispose()
        pewSound.dispose()
    }
}
