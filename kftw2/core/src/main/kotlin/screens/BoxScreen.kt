package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import injection.Ctx
import ktx.app.KtxScreen
import ktx.app.use


/**
 * Created by TommieN on 4/5/2018.
 */

class BoxScreen : KtxScreen {
  val batch = Ctx.context.inject<Batch>()
  val camera = Ctx.context.inject<Camera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)

  var texture: Texture

  var textureWidth: Float
  var textureHeight: Float


  init {

    val drawer = FaceDrawer()
    drawer.drawAll()

    texture = Texture(drawer.pixmap)
    textureWidth = texture.width.toFloat()
    textureHeight = texture.height.toFloat()
    drawer.pixmap.dispose()
  }

  companion object {
    val VIEWPORT_HEIGHT = 320f
    val VIEWPORT_WIDTH = 240f
  }

  override fun render(delta: Float) {
    Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.use {
      batch.draw(texture,
          camera.position.x - textureWidth / 2,
          camera.position.y - textureHeight / 2)
    }
  }

  override fun resize(width: Int, height: Int) {
    super.resize(width, height)
    viewPort.update(width, height)
    batch.projectionMatrix = camera.combined
  }

  override fun pause() {
  }

  override fun dispose() {
    super.dispose()
    batch.dispose()


  }
}

