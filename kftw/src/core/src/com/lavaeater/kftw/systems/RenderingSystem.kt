package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import com.lavaeater.kftw.components.CharacterSpriteComponent
import com.lavaeater.kftw.components.TransformComponent
import ktx.app.use
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class EntityYOrderComparator : Comparator<Entity> {

  val transMpr = mapperFor<TransformComponent>()

  override fun compare(o1: Entity?, o2: Entity?): Int {
    if (o1 != null || o2 != null) {
      val t1 = transMpr[o1]!!
      val t2 = transMpr[o2]!!
      if (t1.position.y == t2.position.y) return 0
      return if (t1.position.y > t2.position.y) -1 else 1
    }
    return 0
  }

}

class RenderCharactersSystem(val batch: SpriteBatch, val camera: OrthographicCamera) :
    SortedIteratingSystem(allOf(CharacterSpriteComponent::class,
        TransformComponent::class).get(), EntityYOrderComparator()) {
  val transformMapper = mapperFor<TransformComponent>()
  val spriteMapper = mapperFor<CharacterSpriteComponent>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
    val transform = transformMapper[entity]
    val spriteComponent = spriteMapper[entity]
    val sprite = Assets.sprites[spriteComponent.spriteKey]!!.entries.first().value //Just to test it
    sprite.setPosition(transform.position.x - sprite.width / 2, transform.position.y - sprite.height / 3)
    batch.projectionMatrix = camera.combined

    sprite.draw(batch)
  }

  override fun update(deltaTime: Float) {
    forceSort()
    batch.use {
      super.update(deltaTime)
    }
  }
}