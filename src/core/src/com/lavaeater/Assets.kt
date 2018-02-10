package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.managers.MainGameManager

/**
 * Created by barry on 12/9/15 @ 11:17 PM.
 */
object Assets : Disposable {
    lateinit var am: AssetManager
    val textureAtlas: TextureAtlas by lazy { TextureAtlas("pes/textures.txt") }
    lateinit var darkDirtAtlas: TextureAtlas
    lateinit var desertAtlas: TextureAtlas
    lateinit var dirtAtlas: TextureAtlas
    val sprites = HashMap<String, Sprite>()
    val darkDirtSprites = HashMap<String, Sprite>()
    val desertSprites = HashMap<String, Sprite>()
    val dirtSprites = HashMap<String, Sprite>()

    val pewSound: Sound by lazy { Gdx.audio.newSound(Gdx.files.internal("sound/pew.ogg"))}
    fun load(): AssetManager {
        am = AssetManager()

        Assets.darkDirtAtlas = TextureAtlas("tiles/darkdirt/darkdirt.txt")
        Assets.desertAtlas = TextureAtlas("tiles/desert/desert.txt")
        Assets.dirtAtlas = TextureAtlas("tiles/dirt/dirt.txp")


        for (region in Assets.textureAtlas.regions) {
            val sprite = Assets.textureAtlas.createSprite(region.name)
            if(region.name == "missile01")
                sprite.setSize(sprite.width * MainGameManager.SCALE * 0.7f, sprite.height * MainGameManager.SCALE * 0.7f) //Map items are 4 times the size
            else if(region.name != "ship")
                sprite.setSize(sprite.width * MainGameManager.SCALE * 4f, sprite.height * MainGameManager.SCALE * 4f) //Map items are 4 times the size
            else
                sprite.setSize(sprite.width * MainGameManager.SCALE, sprite.height * MainGameManager.SCALE)

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

        for(region in Assets.desertAtlas.regions) {
            if(region.name != "blank") {
                val sprite = Assets.desertAtlas.createSprite(region.name)
                sprite.setSize(64f, 64f)
                sprite.x = i * 64 + 300f
                sprite.y = i * 64 + 300f
                Assets.desertSprites.put(region.name, sprite)
                i++
            }
        }

        for(region in Assets.dirtAtlas.regions) {
            if(region.name != "blank") {
                val sprite = Assets.dirtAtlas.createSprite(region.name)
                sprite.setSize(64f, 64f)
                sprite.x = i * 64 + 300f
                sprite.y = i * 64 + 300f
                Assets.dirtSprites.put(region.name, sprite)
                i++
            }
        }

        return am
    }

    override fun dispose() {
        textureAtlas.dispose()
        pewSound.dispose()
    }
}
