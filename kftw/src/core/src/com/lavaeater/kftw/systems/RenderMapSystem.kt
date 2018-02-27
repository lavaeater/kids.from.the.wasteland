package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.TileKey
import com.lavaeater.kftw.screens.Ctx
import ktx.app.use
import kotlin.math.roundToInt

class RenderMapSystem : EntitySystem(0) {

  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val mapManager = Ctx.context.inject<IMapManager>()

  override fun update(deltaTime: Float) {
    super.update(deltaTime)

    batch.projectionMatrix = camera.combined
    batch.use {
      for (tileAndKey in mapManager.getVisibleTiles(camera.position)) {
        val tile = tileAndKey.value
        val key = tileAndKey.key

        val sprite = Assets.sprites[tile.tileType]!![tile.subType]!!
        sprite.setPosition(key.x * 8f, key.y * 8f)
        sprite.draw(batch)

        if (Assets.codeToExtraTiles.containsKey(tile.shortCode))
          for (extraSprite in Assets.codeToExtraTiles[tile.shortCode]!!) {
            extraSprite.setPosition(key.x * 8f, key.y * 8f)
            extraSprite.draw(batch)
          }
      }
    }
  }
}

fun OrthographicCamera.toTile(factor: Int): TileKey {
  return this.position.toTile(factor)
}

fun Vector3.toTile(factor: Int): TileKey {
  return TileKey(this.tileX(factor), this.tileY(factor));
}

fun Vector2.toTile(factor: Int): TileKey {
  return TileKey(this.tileX(factor), this.tileY(factor))
}

fun Vector2.tileX(factor: Int): Int {
  return (this.x / factor).roundToInt()
}

fun Vector2.tileY(factor: Int): Int {
  return (this.y / factor).roundToInt()
}

fun Vector3.tileX(factor: Int): Int {
  return (this.x / factor).roundToInt()
}

fun Vector3.tileY(factor: Int): Int {
  return (this.y / factor).roundToInt()
}