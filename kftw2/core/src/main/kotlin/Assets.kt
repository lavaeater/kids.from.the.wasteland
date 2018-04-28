package com.lavaeater

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
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

  lateinit var standardFont: BitmapFont

  val IDLE = "idle"
  val WALK = "walk"
  val GESTURE = "gesture"
  val ATTACK = "attack"
  val DEATH = "death"

  val animatedCharacters = mapOf("femalerogue" to TextureAtlas(Gdx.files.internal("chars/frogue/frogue.txp")),
      "orc" to TextureAtlas(Gdx.files.internal("chars/forc/forc.txp")))

  val animatedCharacterSprites = mutableMapOf<String, Map<String, List<Sprite>>>()

  val codeToExtraTiles = mutableMapOf<String, List<Sprite>>()

  val sprites = mutableMapOf<String, HashMap<String, Sprite>>()

  fun load(): AssetManager {
    am = AssetManager()

    initializeMapTiles()
    initializeCharacterSprites()

    initAnimatedCharacterSprites()

    initializeFonts()

    return am
  }

  private fun initializeFonts() {
    val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"))

    val fontParams = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
      color = Color.GRAY
      size = 6
    }

    standardFont =  fontGenerator.generateFont(fontParams)

    fontGenerator.dispose()
  }

  private fun initAnimatedCharacterSprites() {
    //We group the animations, this is good
    val width = 6f
    val height = 7f
    val finalMap = mutableMapOf<String, MutableMap<String, MutableList<Sprite>>>()
    for (atlasMap in animatedCharacters) {
      val atlas = atlasMap.value

      val spriteCollection = hashMapOf<String, MutableList<Sprite>>()
      finalMap[atlasMap.key] = spriteCollection

      spriteCollection[IDLE] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(IDLE) }) {
        createAndAddSprite(spriteCollection, atlas, region, width, height, IDLE)
      }
      spriteCollection[WALK] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(WALK) }) {
        createAndAddSprite(spriteCollection, atlas, region, width, height, WALK)
      }
      spriteCollection[GESTURE] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(GESTURE) }) {
        createAndAddSprite(spriteCollection, atlas, region, width, height, GESTURE)
      }

      spriteCollection[ATTACK] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(ATTACK) }) {
        createAndAddSprite(spriteCollection, atlas, region, width, height, ATTACK)
      }

      spriteCollection[DEATH] = mutableListOf()
      for (region in atlas.regions.filter { it.name.contains(DEATH) }) {
        createAndAddSprite(spriteCollection, atlas, region, width, height, DEATH)
      }
    }
    animatedCharacterSprites.putAll(finalMap)
  }

  private fun createAndAddSprite(spriteCollection: HashMap<String, MutableList<Sprite>>, atlas: TextureAtlas, region: TextureAtlas.AtlasRegion, width: Float, height: Float, spriteKey: String) {
    spriteCollection[spriteKey]!!.add(atlas.createSprite(region.name).apply {
      setSize(width, height)
      setOriginCenter()
    })
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
