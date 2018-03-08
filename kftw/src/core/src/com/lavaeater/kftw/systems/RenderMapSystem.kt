package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.lavaeater.Assets
import com.lavaeater.kftw.map.IMapManager
import com.lavaeater.kftw.map.TileKey
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import com.lavaeater.kftw.map.TileFog
import ktx.app.use
import kotlin.math.roundToInt

class RenderMapSystem(val fogOfWar:Boolean = false) : EntitySystem(0) {

  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val mapManager = Ctx.context.inject<IMapManager>()

  override fun update(deltaTime: Float) {
    super.update(deltaTime)
    if(fogOfWar) renderMapWithFogOfWar() else {
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

  fun renderMapWithFogOfWar() {
    batch.projectionMatrix = camera.combined
    batch.use {

      for(renderableTile in mapManager.getVisibleTilesWithFog(camera.position)) {
        val sprite = Assets.sprites[renderableTile.tile.tileType]!![renderableTile.tile.subType]!!

        sprite.setPosition(renderableTile.key.x * 8f, renderableTile.key.y * 8f)
        when(renderableTile.fogStatus) {
          TileFog.NotSeen -> sprite.color = Color.BLACK
          TileFog.Seen -> sprite.color = Color.GRAY
          TileFog.Seeing -> sprite.color = Color.WHITE
        }
        sprite.draw(batch)
        if (Assets.codeToExtraTiles.containsKey(renderableTile.tile.shortCode))
          for (extraSprite in Assets.codeToExtraTiles[renderableTile.tile.shortCode]!!) {
            extraSprite.setPosition(renderableTile.key.x * 8f, renderableTile.key.y * 8f)
            when(renderableTile.fogStatus) {
              TileFog.NotSeen -> extraSprite.color = Color.BLACK
              TileFog.Seen -> extraSprite.color = Color.GRAY
              TileFog.Seeing -> extraSprite.color = Color.WHITE
            }
            extraSprite.draw(batch)
          }
      }
    }
  }
}

fun OrthographicCamera.toTile(factor: Int): TileKey {
  return this.position.toTile(factor)
}

fun Vector3.toTile(factor: Int = GameManager.TILE_SIZE): TileKey {
  return TileKey(this.tileX(factor), this.tileY(factor));
}

fun Vector2.toTile(factor: Int = GameManager.TILE_SIZE): TileKey {
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