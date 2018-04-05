package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxScreen
import ktx.app.use
import java.util.*

/**
 * Created by TommieN on 4/5/2018.
 */

class PortraitScreen : KtxScreen {
  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val pixMap = Pixmap(64, 64, Pixmap.Format.RGBA4444)

  var texture : Texture

  val stack = Stack<Feature>()

  init {
    //1. Draw a black rectangle on the pixmap!

    pixMap.setColor(Color.BLACK)
    pixMap.fillRectangle(0,0, pixMap.width, pixMap.height)
    pixMap.blending = Pixmap.Blending.None

    //2. Add some features to the f-ing stack!

    stack.push(FeatureBase(pixMap, width = 0.7f, height = 1f))

    for(feature in stack) {
      feature.draw()
    }

    texture = Texture(pixMap)
    pixMap.dispose()
  }

  companion object {
    val VIEWPORT_HEIGHT = 64f
    val VIEWPORT_WIDTH = 64f
  }

  override fun render(delta: Float) {
    Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.use {
      batch.draw(texture,0f, 0f)
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

interface Feature {
  fun draw()
}

open class FeatureBase(val pixmap: Pixmap,
                       val margin: Float = 0.05f,
                       val width: Float = 1f,
                       val height: Float = 1f,
                       val color: Color = Color.PINK) : Feature {

  val xMargin = (margin * pixmap.width).toInt()
  val yMargin = (margin * pixmap.height).toInt()
  init {
    //What what?
    /*
    Margin is offlimits, the rest is calculated!

    How do we handle the origins and stuff? We'll deal with that later.

    So the basic shape is a rectangle. It's size, in this case, will be pixmap
    minus margin

    Culling of values? Should width be able to be more than 1? No, absolutely not!
     */

    if (width > 1f || height > 1f)
      throw Exception("Width and Height must be less than 1.0")
  }

  override fun draw() {
    //Draw a square, motherfucker
    val actualWidth = (pixmap.width - (xMargin* 2) * width).toInt()
    val actualHeight = (pixmap.height - (yMargin * 2) * height).toInt()
    //What is origin? Well, top left + margin, etc etc. For this particular square, that is.

    pixmap.setColor(color)
    pixmap.fillRectangle(xMargin,yMargin, actualWidth, actualHeight)
  }
}