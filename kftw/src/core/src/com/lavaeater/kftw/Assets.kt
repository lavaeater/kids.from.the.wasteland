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

  val characters = mapOf(
      "townsfolk" to TextureAtlas(Gdx.files.internal("chars/mtownsfolk/mtownsfolk.txp")),
      "femaleranger" to TextureAtlas(Gdx.files.internal("chars/franger/franger.txp"))
  )

  val IDLE = "idle"
  val WALK = "walk"
  val GESTURE = "gesture"
  val ATTACK = "attack"
  val DEATH = "death"

  val animatedCharacters = mapOf("femalerogue" to TextureAtlas(Gdx.files.internal("chars/frogue/frogue.txp")))

  val animatedCharacterSprites = mutableMapOf<String, Map<String, List<Sprite>>>()

  val codeToExtraTiles = mutableMapOf<String, List<Sprite>>()

  val sprites = mutableMapOf<String, HashMap<String, Sprite>>()

  fun load(): AssetManager {
    am = AssetManager()

    initializeMapTiles()
    initializeCharacterSprites()

    initAnimatedCharacterSprites()

    return am
  }

  private fun initAnimatedCharacterSprites() {
    //We group the animations, this is good
    val finalMap = mutableMapOf<String, MutableMap<String, MutableList<Sprite>>>()
    for(atlasMap in animatedCharacters) {
      val atlas = atlasMap.value

      val spriteCollection = hashMapOf<String, MutableList<Sprite>>()
      finalMap[atlasMap.key] = spriteCollection

      spriteCollection[IDLE] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(IDLE) }) {
        spriteCollection[IDLE]!!.add(atlas.createSprite(region.name))
      }
      spriteCollection[WALK] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(WALK) }) {
        spriteCollection[WALK]!!.add(atlas.createSprite(region.name))
      }
      spriteCollection[GESTURE] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(GESTURE) }) {
        spriteCollection[GESTURE]!!.add(atlas.createSprite(region.name))
      }

      spriteCollection[ATTACK] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(ATTACK) }) {
        spriteCollection[ATTACK]!!.add(atlas.createSprite(region.name))
      }

      spriteCollection[DEATH] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(DEATH) }) {
        spriteCollection[DEATH]!!.add(atlas.createSprite(region.name))
      }
    }
    animatedCharacterSprites.putAll(finalMap)
  }

  private fun initializeCharacterSprites() {
    for (atlasMap in characters) {
      val atlas = atlasMap.value
      sprites.put(atlasMap.key, hashMapOf())
      for (region in atlas.regions) {
        val sprite = atlas.createSprite(region.name)
        sprite.setSize(4f, 4.5f)
        sprites[atlasMap.key]!!.put(region.name, sprite)
      }
    }
  }

  private fun initializeMapTiles() {
    var yFactor = 1f
    var xFactor = 1f
    for (atlasMap in atlases) {
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
  }

  override fun dispose() {
    for (atlas in atlases.values)
      atlas.dispose()
  }
}
