package util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import kotlin.math.roundToInt


public fun fromHSV(h: Float, s: Float, v: Float): Color {
  var h = h
  var s = s
  var v = v
  val r: Int
  val g: Int
  val b: Int
  val i: Int
  val f: Float
  val p: Float
  val q: Float
  val t: Float
  h = Math.max(0.0, Math.min(360.0, h.toDouble())).toFloat()
  s = Math.max(0.0, Math.min(100.0, s.toDouble())).toFloat()
  v = Math.max(0.0, Math.min(100.0, v.toDouble())).toFloat()
  s /= 100f
  v /= 100f

  h /= 60f
  i = Math.floor(h.toDouble()).toInt()
  f = h - i
  p = v * (1 - s)
  q = v * (1 - s * f)
  t = v * (1 - s * (1 - f))
  when (i) {
    0 -> {
      r = Math.round(255 * v)
      g = Math.round(255 * t)
      b = Math.round(255 * p)
    }
    1 -> {
      r = Math.round(255 * q)
      g = Math.round(255 * v)
      b = Math.round(255 * p)
    }
    2 -> {
      r = Math.round(255 * p)
      g = Math.round(255 * v)
      b = Math.round(255 * t)
    }
    3 -> {
      r = Math.round(255 * p)
      g = Math.round(255 * q)
      b = Math.round(255 * v)
    }
    4 -> {
      r = Math.round(255 * t)
      g = Math.round(255 * p)
      b = Math.round(255 * v)
    }
    else -> {
      r = Math.round(255 * v)
      g = Math.round(255 * p)
      b = Math.round(255 * q)
    }
  }

  return Color(r / 255.0f, g / 255.0f, b / 255.0f, 1f)
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