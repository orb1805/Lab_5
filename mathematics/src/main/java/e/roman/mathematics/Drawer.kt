package e.roman.mathematics

import android.graphics.*
import android.widget.ImageView
import kotlin.math.abs
import kotlin.math.pow

class Drawer(
    private val width: Int = 1000,
    private val height: Int = 1000,
    private val imageView: ImageView,
    private val backgroundColor: Int = Color.WHITE
) {

    private val paint = Paint()
    private val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val tablesToDraw = mutableListOf<Pair<MutableList<Pair<Float, Float>>, Int>>()
    private var minX: Float? = null
    private var minY: Float? = null
    private var maxX: Float? = null
    private var maxY: Float? = null

    init {
        imageView.setImageBitmap(bitmap)
    }

    fun drawFunction(table: List<Table2DFunctionSnapshot>, color: Int = Color.BLACK): Drawer {
        paint.color = color
        val tableToDraw = mutableListOf<Pair<Float, Float>>()
        table.forEach {
            tableToDraw += it.x to it.value
        }
        val maxY = tableToDraw.maxByOrNull { it.second }?.second ?: return this
        val minY = tableToDraw.minByOrNull { it.second }?.second ?: return this
        val maxX = tableToDraw.last().first
        val minX = tableToDraw.first().first
        tablesToDraw += tableToDraw to color
        if (minX < this.minX ?: (minX + 1f))
            this.minX = minX
        if (minY < this.minY ?: (minY + 1f))
            this.minY = minY
        if (maxX > this.maxX ?: (maxX - 1f))
            this.maxX = maxX
        if (maxY > this.maxY ?: (maxY - 1f))
            this.maxY = maxY
        return this
    }

    fun drawFunctionFromPairs(table: List<Pair<Float, Float>>, color: Int = Color.BLACK): Drawer {
        paint.color = color
        /*val tableToDraw = mutableListOf<Pair<Float, Float>>()
        table.forEach {
            tableToDraw += it.x to it.value
        }*/
        val tableToDraw = table
        val maxY = tableToDraw.maxByOrNull { it.second }?.second ?: return this
        val minY = tableToDraw.minByOrNull { it.second }?.second ?: return this
        val maxX = tableToDraw.last().first
        val minX = tableToDraw.first().first
        tablesToDraw += tableToDraw.toMutableList() to color
        if (minX < this.minX ?: (minX + 1f))
            this.minX = minX
        if (minY < this.minY ?: (minY + 1f))
            this.minY = minY
        if (maxX > this.maxX ?: (maxX - 1f))
            this.maxX = maxX
        if (maxY > this.maxY ?: (maxY - 1f))
            this.maxY = maxY
        return this
    }

    fun drawFunction(
        function: ((Float, Float) -> Float),
        coordinates: List<Table2DFunctionSnapshot>,
        color: Int = Color.BLACK
    ): Drawer {
        val tableToDraw = mutableListOf<Pair<Float, Float>>()
        paint.color = color
        coordinates.forEach {
            tableToDraw += it.x to function(it.x, it.y)
        }
        val maxY = tableToDraw.maxByOrNull { it.second }?.second ?: return this
        val minY = tableToDraw.minByOrNull { it.second }?.second ?: return this
        val maxX = tableToDraw.last().first
        val minX = tableToDraw.first().first
        tablesToDraw += tableToDraw to color
        if (minX < this.minX ?: (minX + 1f))
            this.minX = minX
        if (minY < this.minY ?: (minY + 1f))
            this.minY = minY
        if (maxX > this.maxX ?: (maxX - 1f))
            this.maxX = maxX
        if (maxY > this.maxY ?: (maxY - 1f))
            this.maxY = maxY
        return this
    }

    fun drawErrorFunction(
        table: List<List<Table2DFunctionSnapshot>>,
        function: ((Float, Float) -> Float),
        color: Int = Color.BLACK
    ): Drawer {
        var tableToDraw = mutableListOf<Pair<Float, Float>>()
        paint.color = color
        table.forEach { tList ->
            val max = tList.maxByOrNull { abs(it.value - function(it.x, it.y)) } ?: return@forEach
            val maxError = abs(max.value - function(max.x, max.y))
            tableToDraw += max.y to maxError
        }
        tableToDraw = tableToDraw.filter { it.second.isFinite() && it.second < 10f.pow(5) }.toMutableList()
        val maxY = tableToDraw.maxByOrNull { it.second }?.second ?: return this
        val minY = tableToDraw.minByOrNull { it.second }?.second ?: return this
        val maxX = tableToDraw.last().first
        val minX = tableToDraw.first().first
        tablesToDraw += tableToDraw to color
        if (minX < this.minX ?: (minX + 1f))
            this.minX = minX
        if (minY < this.minY ?: (minY + 1f))
            this.minY = minY
        if (maxX > this.maxX ?: (maxX - 1f))
            this.maxX = maxX
        if (maxY > this.maxY ?: (maxY - 1f))
            this.maxY = maxY
        return this
    }

    fun draw(xSign: String = "x", ySign: String = "y"/*, minX: Float = 0f, minY: Float = 0f, maxX: Float = 1f, maxY: Float = 1f*/) {
        minX ?: return
        minY ?: return
        maxX ?: return
        maxY ?: return
        /*this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this. maxY = maxY*/
        val multY = (height - FONT_SIZE_VERTICAL * 2f) / (maxY!! - minY!!)
        val multX = width / (maxX!! - minX!!)
        canvas.drawColor(backgroundColor)
        tablesToDraw.forEach { (tableToDraw, color) ->
            paint.color = color
            for (i in 1..tableToDraw.lastIndex)
                if (tableToDraw[i - 1].second.isFinite() && tableToDraw[i].second.isFinite())
                    canvas.drawLine(
                        tableToDraw[i - 1].first * multX - minX!!,
                        height - tableToDraw[i - 1].second * multY + minY!! - FONT_SIZE_VERTICAL,
                        tableToDraw[i].first * multX - minX!!,
                        height - tableToDraw[i].second * multY + minY!! - FONT_SIZE_VERTICAL,
                        paint
                    )
        }
        paint.textSize = FONT_SIZE_VERTICAL
        paint.typeface = Typeface.MONOSPACE
        paint.color = Color.BLACK
        canvas.drawText(maxY!!, 0f, FONT_SIZE_VERTICAL * 2f, paint)
        if (abs(minY!!) > 0.0001f)
            canvas.drawText(minY!!, 0f, height, paint)
        canvas.drawText(maxY!! - (maxY!! - minY!!) / 2f, 0f, height / 2f, paint)
        canvas.drawText(maxX!!, width - maxX!!.toString().length * FONT_SIZE_HORIZONTAL, height, paint)
        if (abs(minX!!) > 0.0001f)
            canvas.drawText(minX!!, 0f, height, paint)
        canvas.drawText((maxX!! - (maxX!! - minX!!) / 2f), width / 2f , height, paint)
        imageView.setImageBitmap(bitmap)
        tablesToDraw.clear()
        this.minX = null
        this.minY = null
        this.maxX = null
        this.maxY = null
    }

    companion object {

        const val FONT_SIZE_VERTICAL = 25f
        const val FONT_SIZE_HORIZONTAL = 9f
    }
}