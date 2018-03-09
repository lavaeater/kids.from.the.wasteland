package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import com.lavaeater.kftw.components.CharacterSpriteComponent
import com.lavaeater.kftw.components.TransformComponent
import com.lavaeater.kftw.components.VisibleComponent
import com.lavaeater.kftw.injection.Ctx
import ktx.app.use
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class RenderCharactersSystem() :
    SortedIteratingSystem(
        allOf(CharacterSpriteComponent::class,
            TransformComponent::class,
            VisibleComponent::class).get(), EntityYOrderComparator()) {
  private val transformMapper = mapperFor<TransformComponent>()
  private val spriteMapper = mapperFor<CharacterSpriteComponent>()

  private val batch = Ctx.context.inject<SpriteBatch>()

  override fun processEntity(entity: Entity, deltaTime: Float) {
    val transform = transformMapper[entity]
    val spriteComponent = spriteMapper[entity]
    when(spriteComponent.animated) {
      true -> renderAnimatedCharacter(transform, spriteComponent, deltaTime)
      false -> renderRegularCharacter(transform, spriteComponent)
    }
  }
  private val frameRate = 1f / 6f
  private fun renderAnimatedCharacter(transform: TransformComponent,
                                      spriteComponent: CharacterSpriteComponent,
                                      deltaTime: Float) {

    //Lets animate these at 12 frames per second, as a test.
    val spriteSet = Assets.animatedCharacterSprites[spriteComponent.spriteKey]!![spriteComponent.currentAnim]!!
    spriteComponent.deltaTime += deltaTime
    if(spriteComponent.deltaTime > frameRate)
    {
      spriteComponent.deltaTime = 0f
      val maxIndex = spriteSet.count() - 1
      spriteComponent.currentIndex++
      if(spriteComponent.currentIndex > maxIndex)
        spriteComponent.currentIndex = 0
    }
    val sprite = spriteSet[spriteComponent.currentIndex]

    sprite.setCenter(transform.position.x,
        transform.position.y + sprite.width / 4)

    batch.color = Color.BLACK

    batch.draw(sprite,
        sprite.x,
        sprite.y,
        0f,
        0f,
        sprite.width,
        sprite.height,
        1f,
        .5f,
        30f)
    batch.color = Color.WHITE

    sprite.draw(batch)
  }

  private fun renderRegularCharacter(transform: TransformComponent, spriteComponent: CharacterSpriteComponent) {
    val sprite = Assets.sprites[spriteComponent.spriteKey]!!.entries.first().value //Just to test it
    sprite.setPosition(transform.position.x - sprite.width / 2, transform.position.y - sprite.height / 3)

    sprite.draw(batch)
  }

  override fun update(deltaTime: Float) {
    forceSort()
    batch.use {
      super.update(deltaTime)
    }
  }
}