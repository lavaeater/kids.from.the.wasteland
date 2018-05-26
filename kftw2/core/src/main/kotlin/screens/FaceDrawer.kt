package screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import util.fromHSV
import util.roundToEven

class FaceDrawer(width: Float = 0.6f, height: Float = 0.8f) {

  val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA4444)
  val basePixelWidth = (pixmap.width * width).roundToEven()
  val basePixelHeight = (pixmap.height * height).roundToEven()

  val offsetX = (pixmap.width - basePixelWidth) / 2
  val offsetY = (pixmap.height - basePixelHeight) / 2
  val drawFunctions = mutableListOf<(pixmap: Pixmap) -> Unit>()

  val baseHue = 32f
  val baseSaturation = 32f
  val baseValue = 100f

  val factor = 15f

  val colors = mapOf("base" to fromHSV(baseHue, baseSaturation, baseValue),
      "lighter" to fromHSV(baseHue, baseSaturation - factor, baseValue + factor),
      "lightest" to fromHSV(baseHue, baseSaturation - 2 * factor, baseValue + 2 * factor),
      "darker" to fromHSV(baseHue, baseSaturation + factor, baseValue - factor),
      "darkest" to fromHSV(baseHue, baseSaturation + 2* factor, baseValue - 2* factor))

  init {
    pixmap.setColor(Color.BLACK)
    pixmap.fillRectangle(0, 0, pixmap.width, pixmap.height)
    pixmap.blending = Pixmap.Blending.None

    drawFunctions.add(::drawBase)
    drawFunctions.add(::drawEyes)
    drawFunctions.add(::drawNose)
    drawFunctions.add(::drawMouth)
    drawFunctions.add(::drawHair)
    drawFunctions.add(::drawExtra)
  }

  fun drawAll() {
    for (drawFunction in drawFunctions)
      drawFunction(pixmap)
  }

  fun drawBase(p: Pixmap) {

    /*
    This is amazing. We can just use

    any number of functions to draw parts of the face with variable widths and heights!
     */

    val baseF = ::drawIrregularBase
    baseF(p)
  }

  fun drawIrregularBase(p: Pixmap) {

    //TOP
    // One third is forehead
    val half = (basePixelHeight / 2)
    val foreHead = (half / 3)
    val eyeLeve = half - foreHead

    var localOffsetY = offsetY
    var localOffsetX = offsetX

    //slightly smaller forehead?
    var w = basePixelWidth - 8
    localOffsetX = (p.width - w) / 2

    val c = colors["base"]!!

    drawRec(p, c, localOffsetX, localOffsetY, w, foreHead / 2)
    w = basePixelWidth - 4
    localOffsetY += foreHead / 2
    localOffsetX = (p.width - w) / 2

    drawRec(p, c, localOffsetX, localOffsetY, w, foreHead / 2)

    localOffsetY += foreHead / 2

    drawRec(p, c, offsetX, localOffsetY, basePixelWidth, eyeLeve)
    localOffsetY += eyeLeve

    // Two thirds is eye-box, I guess

    //BOTTOM
    val nose = half / 2
    val chin = half - nose

    drawRec(p, c, offsetX, localOffsetY, basePixelWidth, nose)
    localOffsetY += nose
    w = basePixelWidth - 2
    localOffsetX = (p.width - w) / 2

    drawRec(p, c, localOffsetX, localOffsetY, w, chin - 2)
    localOffsetY += chin - 2
    w = basePixelWidth - 6
    localOffsetX = (p.width - w) / 2
    drawRec(p, c, localOffsetX, localOffsetY, w, 2)

  }

  fun drawRectangularBase(p: Pixmap) {
    drawRec(p, Color.valueOf("FFC3AAFF"), offsetX, offsetY, basePixelWidth, basePixelHeight)
  }

  fun drawRec(p: Pixmap, c: Color, x: Int, y: Int, w: Int, h: Int) {
    p.setColor(c)
    p.fillRectangle(x, y, w, h)
  }


  fun drawBaseWithRoundedCorners(p: Pixmap) {
    p.setColor(Color.valueOf("FFC3AAFF"))

    var topRows = (.25 * basePixelHeight).roundToEven()

    var xLength = (.5 * basePixelWidth).roundToEven()
    var leftOver = basePixelWidth - xLength
    var localOffsetX = (leftOver / 2)

    var restRows = basePixelHeight - topRows

    var y = 0
    var currentHeight = 2
    while (y < topRows) {
      p.fillRectangle(offsetX + leftOver / 2, y + offsetY, xLength, currentHeight)
      y += currentHeight
      currentHeight += 1

      xLength += y * 2

      if (xLength > basePixelWidth) xLength = basePixelWidth
      leftOver = basePixelWidth - xLength
    }

    p.fillRectangle(offsetX, offsetY + topRows - 1, basePixelWidth, restRows)
  }

  fun drawEyeBox(p: Pixmap) {
    p.setColor(colors["darker"])

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
    val drawers = listOf<(p: Pixmap) -> Unit>(::drawEyeBox, ::drawEyebrows, ::drawEyes2)
    for (draw in drawers)
      draw(p)
  }

  fun drawEyebrows(p: Pixmap) {
    p.setColor(colors["darkest"])

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
    p.setColor(colors["darker"])

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


  fun drawNose(p: Pixmap) {
    p.setColor(colors["darkest"])

    val noseOffsetY = offsetY  + basePixelHeight / 2
    val noseWidth = 4
    val firstX = offsetX + (basePixelWidth - noseWidth) / 2
    val secondX = firstX + noseWidth
    val thirdX = firstX + noseWidth / 2
    val noseHeight = 4
    val firstY = noseOffsetY
    val secondY = noseOffsetY
    val thirdY = noseOffsetY + noseHeight

    p.fillTriangle(firstX, firstY, secondX, secondY, thirdX, thirdY)


  }

  fun drawMouth(p: Pixmap) {
  }

  fun drawHair(p: Pixmap) {
  }

  fun drawExtra(p: Pixmap) {
  }
}