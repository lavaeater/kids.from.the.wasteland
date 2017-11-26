package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.lavaeater.components.SpriteComponent
import com.lavaeater.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class RenderingSystem(val batch: SpriteBatch, val camera:OrthographicCamera, val sprites: HashMap<String, Sprite>) : IteratingSystem(allOf(SpriteComponent::class, TransformComponent::class).get()) {
    val spriteMapper = mapperFor<SpriteComponent>()
    val transformMapper = mapperFor<TransformComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = transformMapper.get(entity)
        val position = transformComponent.position
        val rotation = transformComponent.rotation
        drawSprite(spriteMapper.get(entity).name, position, rotation)
    }

    fun drawSprite(name: String, position: Vector3, rotation: Float) {
        val sprite = sprites.get(name)!!

        sprite.setPosition(position.x - sprite.width / 2, position.y - sprite.height / 2)
        sprite.setOrigin(sprite.width / 2, sprite.height / 2)
        sprite.rotation = rotation
        sprite.draw(batch)
    }

    override fun update(deltaTime: Float) {
        camera.update(true)
        batch.projectionMatrix = camera.combined
        batch.enableBlending()
        batch.begin()
            super.update(deltaTime)
        batch.end()
    }
}