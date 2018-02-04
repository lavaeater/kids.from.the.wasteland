package com.lavaeater.managers
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.codeandweb.physicseditor.PhysicsShapeCache
import com.lavaeater.Assets
import com.lavaeater.ControllerInfo
import com.lavaeater.Player
import com.lavaeater.components.*
import com.lavaeater.map.GameMap
import com.lavaeater.map.MapLoader
import com.lavaeater.map.MapObject
import com.lavaeater.systems.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2

/**
 * Created by tommie on 2017-09-29.
 */
class MainGameManager(val batch:SpriteBatch = SpriteBatch(), val world: World = World(vec2(y = -10f), true), val engine: PooledEngine = PooledEngine(), val camera: OrthographicCamera = OrthographicCamera(), gameOver: ()-> Unit):Disposable {
    override fun dispose() {
        world.dispose()
        batch.dispose()
        particleManager.dispose()
    }

    val physicsCache = PhysicsShapeCache("pes/bodies.xml")
    val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
    lateinit var map: GameMap
    val mapLoader = MapLoader()
    val particleManager: ParticleManager by lazy {ParticleManager(batch)}

    init {
        engine.addSystem(RenderingSystem(batch, camera, Assets.sprites))
        engine.addSystem(PhysicsSystem(world))
        engine.addSystem(MultiFollowCameraSystem(camera))

        val collisionSystem = CollisionSystem(particleManager::explosionAt)
        world.setContactListener(collisionSystem)
        engine.addSystem(collisionSystem)

        engine.addSystem(HealthSystem(particleManager::explosionAt))
        engine.addSystem(RemovalSystem(world))
        engine.addSystem(PlayerProjectileSystem(this::createShot))
        engine.addSystem(GameStateSystem(gameOver))

        val gamepadInputSystem = GamepadInputSystem()
        Controllers.addListener(gamepadInputSystem)
        engine.addSystem(gamepadInputSystem)
    }

    fun createMap() {
        val mapLoader = MapLoader()
        map = mapLoader.createBasicMap()
        for(mapObject in map.mapObjects) {
            val entity = engine.createEntity()
            val body = createBody(mapObject.name, mapObject.scale)
            body.userData = entity
            body.setTransform(mapObject.position,mapObject.rotation)
            entity.add(BodyComponent(body))
            entity.add(MapComponent())
            entity.add(createSpriteComponent(mapObject.name))
            entity.add(TransformComponent())
            entity.add(ZPositionComponent())
            if(mapObject.name == "frame")
                entity.add(FollowCameraComponent())
            engine.addEntity(entity)
        }
    }

    fun createShot(shooter:Entity, transformPosition: Vector3, rotation: Float) {
        val entity = engine.createEntity()
        entity.add(ProjectileComponent(shooter))
        val rot = rotation - 90f


        val body = createBody("missile01", 0.7f)
        body.userData = entity


        val cos = MathUtils.cosDeg(rot)
        val sin = MathUtils.sinDeg(rot)

        body.setTransform(vec2(transformPosition.x + cos *5f, transformPosition.y +  sin *5f), rotation + 180 * MathUtils.degreesToRadians)
        val vel = vec2(MathUtils.cosDeg(rot), MathUtils.sinDeg(rot)).nor().scl(100f)
        body.linearVelocity = vel

        entity.add(BodyComponent(body))
        entity.add(createSpriteComponent("missile01"))
        entity.add(TransformComponent())
        engine.addEntity(entity)
      }

    fun createBody(name:String, scale:Float = 1f) : Body {
        return physicsCache.createBody(name, world, SCALE * scale, SCALE * scale)
    }

    fun createSpriteComponent(name:String):SpriteComponent {
        val spriteComponent = SpriteComponent(name)

        return spriteComponent
    }

    fun createPlayer(startPoint: MapObject, player: Player, controllerInfo: ControllerInfo): Entity {
        val scale = 1f
        val entity = engine.createEntity()
        val body = createBody("ship", scale)
        body.userData = entity //For the collision system
        body.setTransform(startPoint.position.add(0f, 10f), MathUtils.degreesToRadians * 180f)

        val bodyComponent = BodyComponent(body)
        entity.add(bodyComponent)

        entity.add(createSpriteComponent("ship"))

        entity.add(TransformComponent())
        entity.add(ZPositionComponent())
        entity.add(StateComponent())
        entity.add(FollowCameraComponent())
        entity.add(HealthComponent(player))
        entity.add(ScoreComponent(player))
        if(controllerInfo.isKeyBoardController)
            addInputSystem(entity)
        else
            entity.add(GamePadControllerComponent(controllerInfo.controller!!, player))

        player.entity = entity
        engine.addEntity(entity)

        return entity
    }

    fun addInputSystem(entity: Entity) {
        val inputSystem = KeyboardInputSystem(entity)
        engine.addSystem(inputSystem)
        Gdx.input.inputProcessor = inputSystem
    }

    fun update(delta: Float) {
        engine.update(delta)
        particleManager.renderEffects(delta)
    }

    fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    companion object {
        val VIEWPORT_HEIGHT = 54f
        val MAX_VIEWPORT_HEIGHT = 324f
        val VIEWPORT_WIDTH = 96f
        val MAX_VIEWPORT_WIDTH = 576f
        val SCALE = 0.05f
    }

    fun processAllSystems() {
        for(system in engine.systems)
            system.setProcessing(true)
    }

    fun clearWorld() {
        val bodyFamily = allOf(BodyComponent::class).get()
        val bodyMapper = mapperFor<BodyComponent>()

        val entitiesWithBodies = engine.getEntitiesFor(bodyFamily)
        for(entity in entitiesWithBodies) {
            val bc = bodyMapper.get(entity)
            bc.body.userData = null
            world.destroyBody(bc.body)
        }
        engine.removeAllEntities()
        particleManager.clearAll()
    }

    fun stopProcessing() {
        for(system in engine.systems)
            system.setProcessing(false)
    }
}