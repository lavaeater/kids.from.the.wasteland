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

class BoxScreen : KtxScreen {
  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  val pixMap = Pixmap(64, 64, Pixmap.Format.RGBA4444)

  var texture: Texture

  var textureWidth: Float
  var textureHeight: Float

  val features = mutableListOf<BoxFeature>()

  init {
    //1. Draw a black rectangle on the pixmap!

    pixMap.setColor(Color.BLACK)
    pixMap.fillRectangle(0, 0, pixMap.width, pixMap.height)
    pixMap.blending = Pixmap.Blending.None

    //2. Add some features to the f-ing stack!
    val baseFeature = BoxFeature(width = 0.6f, color = Color.valueOf("FFC3AAFF"))
    features.add(baseFeature)

    for(feature in features)
      feature.draw(pixMap)

    texture = Texture(pixMap)
    textureWidth = texture.width.toFloat()
    textureHeight = texture.height.toFloat()
    pixMap.dispose()
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

data class Box(val x: Int = 0, val y: Int = 0, val width:Int = 56, val height: Int = 56) //df

open class BoxFeature(parentBox: Box = Box(),
                      width: Float = 1f,
                      height: Float = 1f,
                      offsetX: Float = 0f, //No offset from center
                      offsetY: Float = 0f,
                      val color: Color = Color.WHITE) { //No offset from center - offset is fraction of total height
  /*
  Ignore margin, we'll just set a different boundingbox for everything..., right?
   */

  val pixelWidth = (parentBox.width * width).toInt()
  val pixelHeight = (parentBox.height * height).toInt()
  val pixelOffsetX = parentBox.x + ((parentBox.width - pixelWidth) / 2)//(pixelWidth * offsetX / 2).toInt()
  val pixelOffsetY = parentBox.y + ((parentBox.height- pixelHeight) / 2)//(pixelHeight * offsetY / 2).toInt()

  val boundingBox = Box(parentBox.x + pixelOffsetX, //move it in x-axis
      parentBox.y + pixelOffsetY, //move it in y-axis
      pixelWidth,
      pixelHeight)
  val children = mutableListOf<BoxFeature>()

  fun draw(pixmap: Pixmap) {
    pixmap.setColor(color)
    pixmap.fillRectangle(boundingBox.x, boundingBox.y,boundingBox.width, boundingBox.height)
    for(child in children)
      child.draw(pixmap)
  }
}

