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

  var texture: Texture

  var textureWidth: Float
  var textureHeight: Float

  val stack = Stack<Feature>()

  init {
    //1. Draw a black rectangle on the pixmap!

    pixMap.setColor(Color.BLACK)
    pixMap.fillRectangle(0, 0, pixMap.width, pixMap.height)
    pixMap.blending = Pixmap.Blending.None

    //2. Add some features to the f-ing stack!

    var feature = stack.push(Feature(parentWidth = pixMap.width, parentHeight = pixMap.height, width = 0.6f))
    stack.push(ChildFeature(feature, height = 0.2f))
    stack.push(ChildFeature(feature, height = 0.1f, color = Color.valueOf("B48A78FF")))
    stack.push(EyeFeature(feature))

    for (feature in stack) {
      feature.draw(pixMap)
    }

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

class EyeFeature(parent: Feature,
                 width: Float = 0.1f,
                 height: Float = 0.1f,
                 distanceBetweenEyes: Float = 0.5f, //Also a ratio of pixelWidth
                 color: Color = Color.BLUE) : ChildFeature(parent, 0f, width, height, color) {

  val pixelDist = (parent.pixelWidth * distanceBetweenEyes).toInt()
  val firstEyeOriginX = ((parent.pixelWidth - pixelDist) / 2)

  override fun draw(pixmap: Pixmap) {

    pixmap.setColor(color)

    pixmap.fillRectangle(xOrigin + firstEyeOriginX - pixelWidth / 2, yOrigin + pixelHeight / 2, pixelWidth, pixelHeight)
    pixmap.fillRectangle(xOrigin + firstEyeOriginX + pixelDist - pixelWidth / 2, yOrigin + pixelHeight / 2, pixelWidth, pixelHeight)


    pixmap.fillCircle(firstEyeOriginX, yOrigin, pixelHeight)
    pixmap.fillCircle(firstEyeOriginX + pixelDist, yOrigin, pixelHeight)
  }
}

open class ChildFeature(parent: Feature,
                        margin: Float = 0.05f,
                        width: Float = 1f,
                        height: Float = 1f,
                        color: Color = Color.valueOf("EAC086FF"),
                        offsetX: Float = 0.5f,
                        offsetY: Float = 0.5f) : Feature(parent.pixelWidth, parent.pixelHeight, margin, width, height, color, offsetX, offsetY)



open class Feature(val parentWidth: Int = 64,
                   val parentHeight: Int = 64,
                   margin: Float = 0.05f,
                   val width: Float = 1f,
                   val height: Float = 0.8f,
                   val color: Color = Color.valueOf("FFC3AAFF"),
                   offsetX: Float = 0f,
                   offsetY: Float = 0f) {

  val pixelMargin = (margin * parentHeight).toInt()
  val pixelHeight = ((parentHeight - pixelMargin * 2) * height).toInt()
  val pixelWidth = ((parentWidth - pixelMargin * 2) * width).toInt()
  val xOrigin = ((parentWidth - pixelWidth) / 2 + offsetX * pixelWidth).toInt()
  val yOrigin = ((parentHeight - pixelHeight) / 2 + offsetY * pixelHeight).toInt()

  open fun draw(pixmap: Pixmap) {
    //Draw a square, motherfucker
    pixmap.setColor(color)
    pixmap.fillRectangle(xOrigin, yOrigin, pixelWidth, pixelHeight)
  }
}

fun Pixmap.drawEllipse(xc:Int, yc:Int, width: Int, height: Int) {
  val a2 = width * width
  val b2 = height * height
  val fa2 = 4 * a2
  val fb2 = 4 * b2
  var x: Int
  var y: Int
  var sigma: Int

  /* first half */
  x = 0
  y = height
  sigma = 2 * b2 + a2 * (1 - 2 * height)
  while (b2 * x <= a2 * y) {
    this.drawPixel(xc + x, yc + y)
    this.drawPixel(xc - x, yc + y)
    this.drawPixel(xc + x, yc - y)
    this.drawPixel(xc - x, yc - y)
    if (sigma >= 0) {
      sigma += fa2 * (1 - y)
      y--
    }
    sigma += b2 * (4 * x + 6)
    x++
  }

  /* second half */
  x = width
  y = 0
  sigma = 2 * a2 + b2 * (1 - 2 * width)
  while (a2 * y <= b2 * x) {
    this.drawPixel(xc + x, yc + y)
    this.drawPixel(xc - x, yc + y)
    this.drawPixel(xc + x, yc - y)
    this.drawPixel(xc - x, yc - y)
    if (sigma >= 0) {
      sigma += fb2 * (1 - x)
      x--
    }
    sigma += a2 * (4 * y + 6)
    y++
  }
}