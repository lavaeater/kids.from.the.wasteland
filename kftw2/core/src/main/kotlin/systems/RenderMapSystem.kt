package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.PerformanceCounters
import com.lavaeater.kftw.injection.Ctx
import com.lavaeater.kftw.managers.GameManager
import map.IMapManager
import ktx.app.use
import kotlin.math.roundToInt

class RenderMapSystem(val fogOfWar:Boolean = false) : EntitySystem(0) {

  val batch = Ctx.context.inject<Batch>()
  val camera = Ctx.context.inject<Camera>()
  val mapManager = Ctx.context.inject<IMapManager>()
  val counters = Ctx.context.inject<PerformanceCounters>()
  var accruedDelta = 0f

  override fun update(deltaTime: Float) {
    super.update(deltaTime)
//    if(fogOfWar) renderMapWithFogOfWar() else {
    val tileX = camera.position.tileX()
    val tileY = camera.position.tileY()

    batch.projectionMatrix = camera.combined
    batch.use {

      val tilesToRender = mapManager.getVisibleTiles(tileX, tileY)

      for ((x, rows) in tilesToRender.withIndex())
        for ((y, tileInstance) in rows.withIndex()) {
          val xPos = (tileInstance.x * 8).toFloat()
          val yPos = (tileInstance.y * 8).toFloat()

          tileInstance.baseSprite.setPosition(xPos, yPos)
          tileInstance.baseSprite.draw(batch)
          for (extraSprite in tileInstance.extraSprites) {
            extraSprite.setPosition(xPos, yPos)
            extraSprite.draw(batch)
          }
        }
    }
    counters.tick()
    accruedDelta += deltaTime
    if (accruedDelta > 5) {
      counters.counters.map { Gdx.app.log(it.name, it.toString()) }
      accruedDelta = 0f

    }
  }
}
//  }

//  fun renderMapWithFogOfWar() {
//    batch.projectionMatrix = camera.combined
//    batch.use {
//
//      for(renderableTile in mapManager.getVisibleTilesWithFog(camera.position)) {
//        val sprite = Assets.sprites[renderableTile.tile.tileType]!![renderableTile.tile.subType]!!
//
//        sprite.setPosition(renderableTile.key.x * 8f, renderableTile.key.y * 8f)
//        when(renderableTile.fogStatus) {
//          TileFog.NotSeen -> sprite.color = Color.BLACK
//          TileFog.Seen -> sprite.color = Color.GRAY
//          TileFog.Seeing -> sprite.color = Color.WHITE
//        }
//        sprite.draw(batch)
//        if (Assets.codeToExtraTiles.containsKey(renderableTile.tile.shortCode))
//          for (extraSprite in Assets.codeToExtraTiles[renderableTile.tile.shortCode]!!) {
//            extraSprite.setPosition(renderableTile.key.x * 8f, renderableTile.key.y * 8f)
//            when(renderableTile.fogStatus) {
//              TileFog.NotSeen -> extraSprite.color = Color.BLACK
//              TileFog.Seen -> extraSprite.color = Color.GRAY
//              TileFog.Seeing -> extraSprite.color = Color.WHITE
//            }
//            extraSprite.draw(batch)
//          }
//      }
//    }
//  }
//}

fun Vector2.tileX(factor: Int = GameManager.TILE_SIZE): Int {
  return (this.x / factor).roundToInt()
}

fun Vector2.toTile(factor: Int = GameManager.TILE_SIZE):Pair<Int,Int> {
  return Pair(tileX(factor), tileY(factor))
}

fun Vector3.toTile(factor: Int = GameManager.TILE_SIZE):Pair<Int,Int> {
  return Pair(tileX(factor), tileY(factor))
}

fun Vector2.tileY(factor: Int = GameManager.TILE_SIZE): Int {
  return (this.y / factor).roundToInt()
}

fun Vector3.tileX(factor: Int = GameManager.TILE_SIZE): Int {
  return (this.x / factor).roundToInt()
}

fun Vector3.tileY(factor: Int = GameManager.TILE_SIZE): Int {
  return (this.y / factor).roundToInt()
}