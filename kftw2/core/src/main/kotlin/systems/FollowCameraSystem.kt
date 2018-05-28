package systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.lavaeater.kftw.components.TransformComponent
import injection.Ctx
import ktx.ashley.mapperFor

class FollowCameraSystem( trackedEntity : Entity) : EntitySystem(300){
    val camera = Ctx.context.inject<Camera>()
    val transformComponet = mapperFor<TransformComponent>()[trackedEntity]
    val speed = 0.2f
    var y = 0f
    var x = 0f

    override fun update(deltaTime: Float) {
        camera.position.x = MathUtils.lerp(camera.position.x, transformComponet.position.x, speed)
        camera.position.y = MathUtils.lerp(camera.position.y, transformComponet.position.y, speed)
        camera.update(true)
    }
}