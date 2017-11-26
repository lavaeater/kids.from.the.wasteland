package com.lavaeater.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.lavaeater.Assets
import com.lavaeater.Game
import com.lavaeater.components.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2

/**
 * This class handles both collisions and collisionstate for damage etc? No... Or? Aaah
 *         world.setContactListener(object: ContactListener {
**/

class CollisionSystem(val explosionAt:(x:Float, y:Float)-> Unit) : IteratingSystem(allOf(CollisionComponent::class).get()), ContactListener {
    val projectileMapper = mapperFor<ProjectileComponent>()
    val mapMapper = mapperFor<MapComponent>()
    val scoreMapper = mapperFor<ScoreComponent>()
    val collisionMapper = mapperFor<CollisionComponent>()
    val healthMapper = mapperFor<HealthComponent>()
    val spriteMapper = mapperFor<SpriteComponent>()
    val transformMapper = mapperFor<TransformComponent>()

    override fun endContact(contact: Contact?) {
        //Nothing here yet
    }

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        //What kind of contact?
        var contactType = 0 //ship on map
        if(bodyA.isBullet || bodyB.isBullet)
            contactType = 1 //bullet on something
        else if(!bodyA.isBullet && !bodyB.isBullet && bodyA.type == BodyDef.BodyType.DynamicBody && bodyB.type == BodyDef.BodyType.DynamicBody)
            contactType = 2 //ship on ship

        when(contactType) {
            0 -> resolveShipOnMapCollision(bodyA, bodyB)
            1 -> resolveBulletCollision(bodyA, bodyB)
            2 -> resolveShipOnShipCollision(bodyA, bodyB)
        }
        Game.instance.updatePlayerLabels()
    }

    private fun resolveShipOnShipCollision(bodyA: Body, bodyB: Body) {
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity

        //Both entities are FUCKED
        entityA.add(CollisionComponent(bodyB, entityB))
        entityB.add(CollisionComponent(bodyA, entityA))
    }


    private fun resolveBulletCollision(bodyA: Body, bodyB: Body) {
        val bullet = if(bodyA.isBullet) bodyA else bodyB
        val bulletEntity = bullet.userData as Entity

        //I always want explosions at the bullet, so here we go.

        val sprite = Assets.sprites[spriteMapper.get(bulletEntity).name]!!
        val entityPos = transformMapper.get(bulletEntity).position
        val bulletPos = vec2(entityPos.x + sprite.width / 2, entityPos.y + sprite.height / 2)
        explosionAt(bulletPos.x, bulletPos.y)

        val hittee = if(bodyA.isBullet) bodyB else bodyA

        //We might have hit a wall. In that case, the bullet should go away!
        val hitteeEntity = hittee.userData as Entity
        if(mapMapper.has(hitteeEntity)) {


            bulletEntity.add(RemovalComponent()) //Just remove the bullet
            return //early exitbull
        }
        //we hit something else
        hitteeEntity.add(CollisionComponent(bullet, bulletEntity))
}

    private fun resolveShipOnMapCollision(bodyA: Body, bodyB: Body) {
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity

        val bodyAisMap = mapMapper.has(entityA)
        val shipBody = if(bodyAisMap) bodyB else bodyA
        val mapBody = if(bodyAisMap) bodyA else bodyB
        val shipEntity = if(bodyAisMap) entityB else entityA
        val mapEntity = if(bodyAisMap) entityA else entityB
        val rotation = shipBody.angle * MathUtils.radiansToDegrees - 90f
        if(rotation < 80 || rotation > 100) {
            shipEntity.add(CollisionComponent(mapBody, mapEntity))
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        //Nothing here
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        //Nothing here
    }

    private val explosionQueue: Array<Entity>

    init {
        explosionQueue = Array<Entity>()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val healthComponent = healthMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)!!
        if(mapMapper.has(collisionComponent.entity)) {
            //Take some damage from the hit
            healthComponent?.player!!.takeDamage(2)
        }
        if(projectileMapper.has(collisionComponent.entity)) {
            val projectile = collisionComponent.entity
            val hitterEntity = projectileMapper.get(projectile).shooter
            scoreMapper.get(hitterEntity)?.player!!.hits++
            if(healthComponent != null)
                healthComponent.player.takeDamage(1)
            projectile.add(RemovalComponent())
        }

        if(!mapMapper.has(collisionComponent.entity) && !projectileMapper.has(collisionComponent.entity)) {
            healthComponent?.player!!.takeDamage(5)
        }

        entity.remove(CollisionComponent::class.java) //remove the collision
    }
}