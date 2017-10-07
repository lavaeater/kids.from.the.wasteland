package com.lavaeater

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import com.lavaeater.managers.WorldManager

object Assets : Disposable {
    lateinit var am: AssetManager
    val tileAtlas: TextureAtlas by lazy { TextureAtlas("tiles/tiles.txt") }
    val sprites = HashMap<String, Sprite>()

//    val pewSound: Sound by lazy { Gdx.audio.newSound(Gdx.files.internal("sound/pew.ogg"))}
    fun load(): AssetManager {
        am = AssetManager()
        for (region in Assets.tileAtlas.regions) {
            val sprite = Assets.tileAtlas.createSprite(region.name)
//            if(region.name == "missile01")
//                sprite.setSize(sprite.width * WorldManager.SCALE * 0.7f, sprite.height * WorldManager.SCALE * 0.7f) //HexMap items are 4 times the size
//            else if(region.name != "ship")
//                sprite.setSize(sprite.width * WorldManager.SCALE * 4f, sprite.height * WorldManager.SCALE * 4f) //HexMap items are 4 times the size
//            else
            sprite.setSize(sprite.width * WorldManager.SCALE, sprite.height * WorldManager.SCALE)

            sprites.put(region.name, sprite)
        }
        return am
    }

    override fun dispose() {
        tileAtlas.dispose()
//        pewSound.dispose()
    }
}
