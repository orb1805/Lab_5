package e.roman.lab_5

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import e.roman.mathematics.Drawer
import e.roman.mathematics.Table2DFunctionSnapshot
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

class Lab5Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab5)

        val functionDrawer = Drawer(1000, 1000, findViewById(R.id.image_view))
        val errorDrawer = Drawer(1000, 500, findViewById(R.id.image_view_error))
        val tvUnstableSign = findViewById<TextView>(R.id.tv_unstable_sign)
        var u: List<List<Table2DFunctionSnapshot>>? = null
        var index = 0

        findViewById<Button>(R.id.btn_explicit).setOnClickListener {
            index = 0
            val phi0 = findViewById<TextView>(R.id.et_u_0_t).text.toString().toFloat()
            val phi1 = findViewById<TextView>(R.id.et_u_l_t).text.toString().toFloat()
            val psi = findViewById<TextView>(R.id.et_u_x_0).text.toString().toFloat()
            val xStepsCount = findViewById<TextView>(R.id.et_x_steps).text.toString().toInt()
            val tStepsCount = findViewById<TextView>(R.id.et_t_steps).text.toString().toInt()
            val l = findViewById<TextView>(R.id.et_l).text.toString().toFloat()
            val t = findViewById<TextView>(R.id.et_t).text.toString().toFloat()
            val h = l / xStepsCount
            val tau = t / tStepsCount
            if (tau / h.pow(2) > 0.5f)
                tvUnstableSign.visibility = TextView.VISIBLE
            else
                tvUnstableSign.visibility = TextView.INVISIBLE
            u = ParabolicSolver.explicitFiniteDifferenceSolver(
                l,
                t,
                xStepsCount,
                tStepsCount,
                { phi0 },
                { phi1 },
                { psi },
                1f
            ) { x, _ -> sin(PI.toFloat() * x) }
            functionDrawer
                .drawFunction(u!![0], Color.RED)
                .drawFunction(ParabolicSolver.Companion::actualFunction, u!![0], Color.BLUE)
                .draw()
            errorDrawer
                .drawErrorFunction(u!!, ParabolicSolver.Companion::actualFunction)
                .draw()
        }

        findViewById<Button>(R.id.btn_implicit).setOnClickListener {
            index = 0
            val phi0 = findViewById<TextView>(R.id.et_u_0_t).text.toString().toFloat()
            val phi1 = findViewById<TextView>(R.id.et_u_l_t).text.toString().toFloat()
            val psi = findViewById<TextView>(R.id.et_u_x_0).text.toString().toFloat()
            val xStepsCount = findViewById<TextView>(R.id.et_x_steps).text.toString().toInt()
            val tStepsCount = findViewById<TextView>(R.id.et_t_steps).text.toString().toInt()
            val l = findViewById<TextView>(R.id.et_l).text.toString().toFloat()
            val t = findViewById<TextView>(R.id.et_t).text.toString().toFloat()
            tvUnstableSign.visibility = TextView.INVISIBLE
            u = ParabolicSolver.implicitFiniteDifferenceSolver(
                l,
                t,
                xStepsCount,
                tStepsCount,
                { phi0 },
                { phi1 },
                { psi },
                1f
            ) { x, _ -> sin(PI.toFloat() * x) }
            functionDrawer
                .drawFunction(u!![0], Color.RED)
                .drawFunction(ParabolicSolver.Companion::actualFunction, u!![0], Color.BLUE)
                .draw()
            errorDrawer
                .drawErrorFunction(u!!, ParabolicSolver.Companion::actualFunction)
                .draw()
        }

        findViewById<Button>(R.id.btn_crank_nicholson).setOnClickListener {
            index = 0
            val phi0 = findViewById<TextView>(R.id.et_u_0_t).text.toString().toFloat()
            val phi1 = findViewById<TextView>(R.id.et_u_l_t).text.toString().toFloat()
            val psi = findViewById<TextView>(R.id.et_u_x_0).text.toString().toFloat()
            val xStepsCount = findViewById<TextView>(R.id.et_x_steps).text.toString().toInt()
            val tStepsCount = findViewById<TextView>(R.id.et_t_steps).text.toString().toInt()
            val l = findViewById<TextView>(R.id.et_l).text.toString().toFloat()
            val t = findViewById<TextView>(R.id.et_t).text.toString().toFloat()
            tvUnstableSign.visibility = TextView.INVISIBLE
            u = ParabolicSolver.crankNicholsonSolver(
                l,
                t,
                xStepsCount,
                tStepsCount,
                { phi0 },
                { phi1 },
                { psi },
                1f
            ) { x, _ -> sin(PI.toFloat() * x) }
            functionDrawer
                .drawFunction(u!![0], Color.RED)
                .drawFunction(ParabolicSolver.Companion::actualFunction, u!![0], Color.BLUE)
                .draw()
            errorDrawer
                .drawErrorFunction(u!!, ParabolicSolver.Companion::actualFunction)
                .draw()
        }

        findViewById<Button>(R.id.btn_left).setOnClickListener {
            u ?: return@setOnClickListener
            if (index in 1..u!!.lastIndex)
                index--
            else
                index = u!!.lastIndex
            functionDrawer
                .drawFunction(u!![index], Color.RED)
                .drawFunction(ParabolicSolver.Companion::actualFunction, u!![index], Color.BLUE)
                .draw()
        }

        findViewById<Button>(R.id.btn_right).setOnClickListener {
            u ?: return@setOnClickListener
            if (index in 0 until u!!.lastIndex)
                index++
            else
                index = 0
            functionDrawer
                .drawFunction(u!![index], Color.RED)
                .drawFunction(ParabolicSolver.Companion::actualFunction, u!![index], Color.BLUE)
                .draw()
        }
    }
}