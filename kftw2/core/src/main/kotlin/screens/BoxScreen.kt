package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.lavaeater.kftw.injection.Ctx
import ktx.app.KtxScreen
import ktx.app.use
import com.badlogic.gdx.graphics.Pixmap
import kotlin.math.pow
import kotlin.math.roundToInt


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

data class Box(val x: Int = 4, val y: Int = 4, val width: Int = 56, val height: Int = 56) //df

class FaceDrawer(width: Float = 0.6f, height: Float = 0.8f) {

  val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA4444)


  val basePixelWidth = (pixmap.width * width).roundToEven()
  val basePixelHeight = (pixmap.height * height).roundToEven()


  val offsetX = (pixmap.width - basePixelWidth) / 2
  val offsetY = (pixmap.height - basePixelHeight) / 2
  val drawFunctions = mutableListOf<(pixmap: Pixmap) -> Unit>()

  init {
    pixmap.setColor(Color.BLACK)
    pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)
    pixmap.blending = Pixmap.Blending.None

    drawFunctions.add(::drawBaseWithRoundedCorners)
    drawFunctions.add(::drawEyeBox)
    drawFunctions.add(::drawEyebrows)
    drawFunctions.add(::drawEyes2)

    /*
    We should always work with powers of 2.

    SO, our pixmap is 64x 64, the ratios in that should be
    a) divisible by 2, never odd
    b) powers of two? What is a third of 64?
    21,3, that's what!

    So... the face should be some even multiplier of two, at least
    How do we round an integer to the nearest even number?

    Rounding should be either up or down, so, 20.9 = 20, 21.3 = 22.


     */
  }

  fun drawAll() {
    for (drawFunction in drawFunctions)
      drawFunction(pixmap)
  }

  fun drawBase(p: Pixmap) {
    p.setColor(Color.valueOf("FFC3AAFF"))
    p.fillRectangle(offsetX, offsetY, basePixelWidth, basePixelHeight)
  }


  fun drawBaseWithRoundedCorners(p: Pixmap) {
    p.setColor(Color.valueOf("FFC3AAFF"))

    var topRows = (.25 *basePixelHeight).roundToEven()

    var xLength = (.5 * basePixelWidth).roundToEven()
    var leftOver = basePixelWidth - xLength
    var localOffsetX = (leftOver / 2)

    var restRows = basePixelHeight-topRows

    var y = 0
    var currentHeight = 2
    while(y < topRows) {
      p.fillRectangle(offsetX + leftOver / 2, y + offsetY, xLength, currentHeight)
      y += currentHeight
      currentHeight += 1

      xLength += y * 2

      if(xLength > basePixelWidth) xLength = basePixelWidth
      leftOver = basePixelWidth-xLength
    }

    p.fillRectangle(offsetX, offsetY+ topRows -1, basePixelWidth, restRows)
  }

  fun drawEyeBox(p: Pixmap) {
    p.setColor(Color.valueOf("D2A18CFF"))

    val w = 0.8f
    val h = 0.25f

    val offsetYFactor = -0.2f
    val offsetXFactor = 0f

    val pW = calcPixel(w, basePixelWidth)
    val pH = calcPixel(h, basePixelHeight)

    val oX = (offsetX + (basePixelWidth - pW) / 2 + (basePixelWidth - pW) / 2 * offsetXFactor).roundToEven()
    val oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(oX, oY, pW, pH)
  }

  fun drawEyes(p: Pixmap) {
    p.setColor(Color.BROWN)

    val w = 0.1f
    val h = 0.1f

    val offsetYFactor = -0.2f
    val offsetXFactor = 0f

    val distanceFactor = 0.5f
    val pixelDistance = (basePixelWidth * distanceFactor).roundToEven()

    val pW = calcPixel(w, basePixelHeight)
    val pH = calcPixel(h, basePixelHeight)

    val firstEyeX = (basePixelWidth - pixelDistance) / 2 + offsetX
    val secondEyeX = firstEyeX + pixelDistance

    val oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(firstEyeX, oY, pW, pH)
    p.fillRectangle(secondEyeX, oY, pW, pH)
  }

  fun drawEyebrows(p: Pixmap) {
    p.setColor(Color.BROWN)

    val w = 0.8f
    val h = 0.1f

    val offsetYFactor = -0.35f
    val offsetXFactor = 0f

    val pW = calcPixel(w, basePixelWidth)
    val pH = calcPixel(h, basePixelHeight)

    val oX = (offsetX + (basePixelWidth - pW) / 2 + (basePixelWidth - pW) / 2 * offsetXFactor).roundToEven()
    val oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(oX, oY, pW, pH)
  }

  fun drawEyes2(p: Pixmap) {
    p.setColor(Color.BROWN)

    var w = 0.25f
    var h = 0.05f

    var offsetYFactor = 0f
    val offsetXFactor = 0f

    val distanceFactor = 0.5f
    val pixelDistance = (basePixelWidth * distanceFactor).roundToEven()

    var pW = calcPixel(w, basePixelWidth)
    var pH = calcPixel(h, basePixelWidth)

    var firstEyeX = (basePixelWidth - pixelDistance) / 2 + offsetX - pW / 2
    var secondEyeX = firstEyeX + pixelDistance

    var oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(firstEyeX, oY, pW, pH)
    p.fillRectangle(secondEyeX, oY, pW, pH)

    //Those were bags
    p.setColor(Color.WHITE)

    w = 0.2f
    h = 0.15f

    pW = calcPixel(w, basePixelWidth)
    pH = calcPixel(h, basePixelWidth)
    offsetYFactor = -0.2f

    firstEyeX = (basePixelWidth - pixelDistance) / 2 + offsetX - pW / 2
    secondEyeX = firstEyeX + pixelDistance

    oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(firstEyeX, oY, pW, pH)
    p.fillRectangle(secondEyeX, oY, pW, pH)

    //Those were whites

    p.setColor(Color.BLUE)
    w = 0.1f
    h = 0.1f

    pW = calcPixel(w, basePixelWidth)
    pH = calcPixel(h, basePixelWidth)

    firstEyeX = (basePixelWidth - pixelDistance) / 2 + offsetX - pW / 2
    secondEyeX = firstEyeX + pixelDistance

    oY = (offsetY + (basePixelHeight - pH) / 2 + (basePixelHeight - pH) / 2 * offsetYFactor).roundToEven()

    p.fillRectangle(firstEyeX, oY, pW, pH)
    p.fillRectangle(secondEyeX, oY, pW, pH)


  }

  fun calcPixel(factor: Float, base: Int) = (factor * base).roundToEven()
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

  val pixelHeight = (parentBox.height * height).roundToEven()
  val pixelWidth = (parentBox.width * width).roundToEven()
  val pixelOffsetX = parentBox.x + ((parentBox.width - pixelWidth) / 2)//(basePixelWidth * offsetX / 2).roundToEven()
  val pixelOffsetY = parentBox.y + ((parentBox.height - pixelHeight) / 2) + ((parentBox.height - pixelHeight) / 2 * offsetY).roundToEven()

  val boundingBox = Box(pixelOffsetX, //move it in x-axis
      pixelOffsetY, //move it in y-axis
      pixelWidth,
      pixelHeight)
  val children = mutableListOf<BoxFeature>()

  fun draw(pixmap: Pixmap) {
    pixmap.setColor(color)
    pixmap.fillRectangle(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
    for (child in children)
      child.draw(pixmap)
  }
}

fun Pixmap.fillRoundedRectangle(x:Int, y:Int, width:Int, height:Int, radius:Int, color:Color) {
  val pixmap = this
  pixmap.setColor(color)

  // Pink rectangle
  pixmap.fillRectangle(x, y + radius, width, height - 2 * radius)

// Green rectangle
  pixmap.fillRectangle(x + radius, y, width -2 * radius, height)


// Bottom-left circle
  pixmap.fillCircle(x + radius, radius, radius)

// Top-left circle
  pixmap.fillCircle(x+ radius, height - radius, radius)

// Bottom-right circle
  pixmap.fillCircle(width - radius, radius, radius)

// Top-right circle
  pixmap.fillCircle(width - radius, height - radius, radius)
}

fun Float.roundToEven():Int {
  return if(this.roundToInt() % 2 == 0) this.roundToInt() else this.roundToInt() + 1 //Good enough?
}

fun Double.roundToEven(): Int {
  return if(this.roundToInt() % 2 == 0) this.roundToInt() else this.roundToInt() + 1 //Good enough?
}
