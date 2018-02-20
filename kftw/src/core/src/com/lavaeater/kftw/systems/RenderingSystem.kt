package com.lavaeater.kftw.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lavaeater.Assets
import com.lavaeater.kftw.components.CharacterSpriteComponent
import com.lavaeater.kftw.components.TransformComponent
import ktx.app.use
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class RenderCharactersSystem(val batch: SpriteBatch, val camera : OrthographicCamera) : IteratingSystem(allOf(CharacterSpriteComponent::class, TransformComponent::class).get()) {
    val transformMapper = mapperFor<TransformComponent>()
    val spriteMapper = mapperFor<CharacterSpriteComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = transformMapper[entity]
        val spriteComponent = spriteMapper[entity]
        val sprite = Assets.sprites[spriteComponent.spriteKey]!!.entries.first().value //Just to test it
        sprite.setPosition(transform.x, transform.y)
        batch.projectionMatrix = camera.combined

        //This might be inefficient.
        batch.use {
            sprite.draw(batch)
        }

    }
}