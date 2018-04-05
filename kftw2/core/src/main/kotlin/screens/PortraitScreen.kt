package screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxScreen
import java.util.*

/**
 * Created by tommie on 2018-04-04.
 */
class PortraitScreen : KtxScreen {
  companion object {
    val VIEWPORT_HEIGHT = 64f
    val VIEWPORT_WIDTH = 64f
  }

  init {
  }

  val featureStack = Stack<Feature>()
  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA4444)

  private fun update(delta:Float) {

  }

  override fun render(delta: Float) {
    update(delta)
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

interface Feature {
  fun draw(pixmap: Pixmap)
}



open class RectangularFeature(val baseColor:Color = Color.PINK,
                              val margin: Float = 0.05f,
                              val pixmapWidth:Int = 64,
                              val pixmapHeight:Int = 64) : Feature {

  init {
    
  }

  override fun draw(pixmap: Pixmap) {
    pixmap.fillRectangle()
  }

}