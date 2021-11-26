package e.roman.mathematics

import android.graphics.Canvas
import android.graphics.Paint
import java.lang.Exception
import kotlin.math.PI
import kotlin.math.abs

fun <T> List<T>.preLast(): T {
    if (size < 2)
        throw Exception("List must be at least size 2")
    return this[lastIndex - 1]
}

fun Canvas.drawText(text: String, x: Int, y: Int, paint: Paint) =
    drawText(text, x.toFloat(), y.toFloat(), paint)

fun Canvas.drawText(text: String, x: Float, y: Int, paint: Paint) =
    drawText(text, x, y.toFloat(), paint)

fun Canvas.drawText(text: String, x: Int, y: Float, paint: Paint) =
    drawText(text, x.toFloat(), y, paint)

fun Canvas.drawText(text: Float, x: Float, y: Float, paint: Paint) {
    when {
        abs(text) < 0.000001f ->
            drawText("0.0", x, y, paint)
        abs(text) > 0.001f ->
            drawText("%.3f".format(text), x, y, paint)
        else -> {
            val string = text.toString()
            val index = string.indexOfFirst { it != '0' && it != '.' }
            if (index != -1)
                drawText("${string[index]}E-${index - 2}", x, y, paint)
        }
    }
}

fun Canvas.drawText(text: Float, x: Int, y: Int, paint: Paint) =
    drawText(text, x.toFloat(), y.toFloat(), paint)

fun Canvas.drawText(text: Float, x: Float, y: Int, paint: Paint) =
    drawText(text, x, y.toFloat(), paint)

fun Canvas.drawText(text: Float, x: Int, y: Float, paint: Paint) =
    drawText(text, x.toFloat(), y, paint)

const val PIF = PI.toFloat()