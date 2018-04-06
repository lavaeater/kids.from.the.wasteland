package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxScreen
import ktx.app.use

/**
 * Created by TommieN on 4/5/2018.
 */

class BoxScreen : KtxScreen {
  val batch = Ctx.context.inject<SpriteBatch>()
  val camera = Ctx.context.inject<OrthographicCamera>()
  val viewPort = ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
  /*val pixMap = Pixmap(64, 64, Pixmap.Format.RGBA4444)*/

  var texture: Texture

  var textureWidth: Float
  var textureHeight: Float

  val features = mutableListOf<BoxFeature>()

  init {
    //1. Draw a black rectangle on the pixmap!

  /*  pixMap.setColor(Color.BLACK)
    pixMap.fillRectangle(0, 0, pixMap.width, pixMap.height)
    pixMap.blending = Pixmap.Blending.None*/

    //2. Add some features to the f-ing stack!
    /*val baseFeature = BoxFeature(width = 0.6f, height = 0.8f, color = Color.valueOf("FFC3AAFF"))
    features.add(baseFeature)

    val eyeBox = BoxFeature(baseFeature.boundingBox,
        width = 0.8f,
        height = 0.2f,
        offsetY = -0.3f,
        color=Color.valueOf("D2A18CFF"))

    baseFeature.children.add(eyeBox)

    for(feature in features)
      feature.draw(pixMap)*/

    val drawer = FaceDrawer(0.6f, 0.8f)
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

data class Box(val x: Int = 4, val y: Int = 4, val width:Int = 56, val height: Int = 56) //df

class FaceDrawer(width: Float = 0.6f, height: Float = 1f) {

  val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA4444)
  val basePixelWidth = (pixmap.width * width).toInt()
  val basePixelHeight = (pixmap.height * height).toInt()
  val offsetX = (pixmap.width - basePixelWidth)/2
  val offsetY = (pixmap.height - basePixelHeight)/2
  val drawFunctions = mutableListOf<(pixmap:Pixmap)->Unit>()

  init {
    pixmap.setColor(Color.BLACK)
    pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)
    pixmap.blending = Pixmap.Blending.None

    drawFunctions.add(::drawBase)
    drawFunctions.add(::drawEyeBox)
    drawFunctions.add(::drawEyes)
  }

  fun drawAll() {
    for(drawFunction in drawFunctions)
      drawFunction(pixmap)
  }

  fun drawBase(p:Pixmap) {
    p.setColor(Color.valueOf("FFC3AAFF"))
    p.fillRectangle(offsetX, offsetY, basePixelWidth, basePixelHeight)
  }

  fun drawEyeBox(p:Pixmap) {
    p.setColor(Color.valueOf("D2A18CFF"))

    val w = 0.8f
    val h = 0.15f

    val offsetYFactor = -0.2f
    val offsetXFactor = 0f

    val pW = (w * basePixelWidth).toInt()
    val pH = (h * basePixelHeight).toInt()

    val oX = (offsetX + (basePixelWidth - pW) / 2 + (basePixelWidth - pW) / 2 * offsetXFactor).toInt()
    val oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).toInt()

    p.fillRectangle(oX, oY, pW, pH)
  }

  fun drawEyes(p:Pixmap) {
    p.setColor(Color.BROWN)

    val w = 0.1f
    val h = 0.1f

    val offsetYFactor = -0.2f
    val offsetXFactor = 0f

    val distanceFactor = 0.5f
    val pixelDistance = (basePixelWidth * distanceFactor).toInt()

    val pW = (w * basePixelHeight).toInt()
    val pH = (h * basePixelHeight).toInt()

    val firstEyeX = (basePixelWidth - pixelDistance) / 2 + offsetX
    val secondEyeX = firstEyeX + pixelDistance

    val oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).toInt()

    p.fillRectangle(firstEyeX, oY, pW, pH)
    p.fillRectangle(secondEyeX, oY, pW, pH)
  }
}

open class BoxFeature(parentBox: Box = Box(),
                      width: Float = 1f,
                      height: Float = 1f,
                      offsetX: Float = 0f, //No offset from center
                      offsetY: Float = 0f,
                      val color: Color = Color.WHITE) { //No offset from center - offset is fraction of total height
  /*
  Ignore margin, we'll just set a different boundingbox for everything..., right?
   */

  val pixelHeight = (parentBox.height * height).toInt()
  val pixelWidth = (parentBox.width * width).toInt()
  val pixelOffsetX = parentBox.x + ((parentBox.width - pixelWidth) / 2)//(basePixelWidth * offsetX / 2).toInt()
  val pixelOffsetY = parentBox.y + ((parentBox.height- pixelHeight) / 2) + ((parentBox.height- pixelHeight) / 2 * offsetY).toInt()

  val boundingBox = Box(pixelOffsetX, //move it in x-axis
      pixelOffsetY, //move it in y-axis
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

