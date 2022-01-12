package e.roman.lab_5

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import e.roman.lab_5.databinding.ActivityLab6Binding
import e.roman.mathematics.Drawer
import e.roman.mathematics.PIF
import e.roman.mathematics.Table2DFunctionSnapshot
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

class Lab6Activity : AppCompatActivity() {

    private lateinit var binding: ActivityLab6Binding
    private lateinit var functionDrawer: Drawer
    private lateinit var errorDrawer: Drawer
    private var index = 0
    private lateinit var tvUnstableSign: TextView
    private lateinit var tvTSign: TextView
    private var u: List<List<Table2DFunctionSnapshot>>? = null
    private var xStepsCount = 0
    private var tStepsCount = 0
    private var l = 0f
    private var t = 0f
    private var hyperbolicSolver: HyperbolicSolver? = null
    private var changed = true
    private val a = 2f
    private val b = 4f

    //private val viewModel = (applicationContext as ViewModelProvider).getViewModel(this::class.qualifiedName!!) as Lab6ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLab6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        functionDrawer = Drawer(1000, 1000, binding.imageView)
        errorDrawer = Drawer(1000, 500, binding.imageViewError)
        tvUnstableSign = binding.tvUnstableSign
        tvTSign = binding.tvTSign

        binding.etXSteps.addTextChangedListener {
            xStepsCount = it.toInt()
            changed = true
        }
        xStepsCount = binding.etXSteps.text.toInt()
        binding.etTSteps.addTextChangedListener {
            tStepsCount = it.toInt()
            changed = true
        }
        tStepsCount = binding.etTSteps.text.toInt()
        binding.etL.addTextChangedListener {
            l = it.toFloat()
            changed = true
        }
        l = binding.etL.text.toFloat()
        binding.etT.addTextChangedListener {
            t = it.toFloat()
            changed = true
        }
        t = binding.etT.text.toFloat()

        binding.btnExplicit.setOnClickListener { drawExplicit() }

        binding.btnImplicit.setOnClickListener { drawImplicit() }

        binding.btnLeft.setOnClickListener { previousSnapshot() }

        binding.btnRight.setOnClickListener { nextSnapshot() }
    }

    private fun drawExplicit() {
        index = 0
        tvTSign.text = "t = 0.0"
        tvTSign.visibility = TextView.VISIBLE
        val h = l / xStepsCount
        val tau = t / tStepsCount
        if (a * (tau / h).pow(2) > 1f)
            tvUnstableSign.visibility = TextView.VISIBLE
        else
            tvUnstableSign.visibility = TextView.INVISIBLE
        initSolver()
        u = hyperbolicSolver!!.explicitSolution
        drawResult()
    }

    private fun drawImplicit() {
        tvUnstableSign.visibility = TextView.INVISIBLE
        tvTSign.text = "t = 0.0"
        tvTSign.visibility = TextView.VISIBLE
        index = 0
        initSolver()
        u = hyperbolicSolver!!.implicitSolution
        drawResult()
    }

    private fun nextSnapshot() {
        u ?: return
        if (index in 0 until u!!.lastIndex)
            index++
        else
            index = 0
        functionDrawer
            .drawFunction(u!![index], Color.RED)
            .drawFunction(HyperbolicSolver.Companion::actualFunction, u!![index], Color.BLUE)
            .draw("x", "u(t,x)")
        tvTSign.text = "t = ${u!![index].first().y}"
    }

    private fun previousSnapshot() {
        u ?: return
        if (index in 1..u!!.lastIndex)
            index--
        else
            index = u!!.lastIndex
        functionDrawer
            .drawFunction(u!![index], Color.RED)
            .drawFunction(HyperbolicSolver.Companion::actualFunction, u!![index], Color.BLUE)
            .draw("x", "u(t,x)")
        tvTSign.text = "t = ${u!![index].first().y}"
    }

    private fun initSolver() {
        if (changed || hyperbolicSolver == null)
            hyperbolicSolver = HyperbolicSolver(
                l = PIF,
                time = t,
                xCount = xStepsCount,
                tCount = tStepsCount,
                psi2 = { x -> exp(-x) * sin(x) },
                a = a,
                b = b
            )
    }

    private fun drawResult() {
        functionDrawer
            .drawFunction(u!![0], Color.RED)
            .drawFunction(HyperbolicSolver.Companion::actualFunction, u!![0], Color.BLUE)
            .draw("x", "u(t,x)")
        errorDrawer
            .drawErrorFunction(u!!, HyperbolicSolver.Companion::actualFunction)
            .draw("t", "error")
    }
}