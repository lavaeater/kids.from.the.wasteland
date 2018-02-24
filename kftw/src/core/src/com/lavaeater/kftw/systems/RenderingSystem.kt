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
    val renderQueue = mutableListOf<Entity>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        renderQueue.add(entity)
        val transform = transformMapper[entity]
        val spriteComponent = spriteMapper[entity]
        val sprite = Assets.sprites[spriteComponent.spriteKey]!!.entries.first().value //Just to test it
        sprite.setPosition(transform.position.x - sprite.width / 2, transform.position.y - sprite.height / 3)
        batch.projectionMatrix = camera.combined

        sprite.draw(batch)
    }

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }
}