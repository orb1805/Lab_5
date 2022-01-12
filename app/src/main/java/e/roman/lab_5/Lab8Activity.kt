package e.roman.lab_5

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import e.roman.lab_5.databinding.ActivityLab8Binding
import e.roman.mathematics.Drawer
import e.roman.mathematics.PIF
import e.roman.mathematics.ThreeDimArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.*

class Lab8Activity : AppCompatActivity() {

    private lateinit var binding: ActivityLab8Binding
    private var N = 10
    private var M = 10
    private var K = 10
    private var T = 1f
    private var a = 1f
    private var xIndex = 0
    private var yIndex = 0
    private lateinit var solver: TwoDimParabolicSolver
    private var u: ThreeDimArray? = null
    private lateinit var analyticalSolution: ThreeDimArray
    private var isChanged = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab8)

        binding = ActivityLab8Binding.inflate(layoutInflater)

        setContentView(binding.root)

        val functionDrawer = Drawer(1000, 1000, binding.imageView)

        with(binding) {
            xSlider.addOnChangeListener { slider, value, _ ->
                u ?: return@addOnChangeListener
                xIndex = (value / slider.stepSize).toInt() - 1
                if (xIndex !in 0 until N || yIndex !in 0 until M)
                    return@addOnChangeListener
                functionDrawer
                    .drawFunctionFromPairs(u!![xIndex].second[yIndex].second, Color.RED)
                    .drawFunctionFromPairs(
                        analyticalSolution[xIndex].second[yIndex].second,
                        Color.BLUE
                    )
                    .draw()
            }
            ySlider.addOnChangeListener { slider, value, _ ->
                u ?: return@addOnChangeListener
                yIndex = (value / slider.stepSize).toInt() - 1
                if (xIndex !in 0 until N || yIndex !in 0 until M)
                    return@addOnChangeListener
                functionDrawer
                    .drawFunctionFromPairs(u!![xIndex].second[yIndex].second, Color.RED)
                    .drawFunctionFromPairs(
                        analyticalSolution[xIndex].second[yIndex].second,
                        Color.BLUE
                    )
                    .draw()
            }
            btnAltering.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    initSolver()
                    u = solver.alternatingDirectionsSolution
                    functionDrawer
                        .drawFunctionFromPairs(u!![0].second[0].second, Color.RED)
                        .drawFunctionFromPairs(analyticalSolution[0].second[0].second, Color.BLUE)
                        .draw()
                    initSliders()
                }
            }
            btnFractional.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    initSolver()
                    u = solver.fractionalStepsSolution
                    functionDrawer
                        .drawFunctionFromPairs(u!![0].second[0].second, Color.RED)
                        .drawFunctionFromPairs(analyticalSolution[0].second[0].second, Color.BLUE)
                        .draw()
                    initSliders()
                }
            }
            etXSteps.addTextChangedListener { isChanged = true }
            etYSteps.addTextChangedListener { isChanged = true }
            etTSteps.addTextChangedListener { isChanged = true }
            etA.addTextChangedListener { isChanged = true }
            etT.addTextChangedListener { isChanged = true }
        }
    }

    private fun initSolver() {
        if (!isChanged) return
        with(binding) {
            N = etXSteps.text.toInt()
            M = etYSteps.text.toInt()
            K = etTSteps.text.toInt()
            T = etT.text.toFloat()
            a = etA.text.toFloat()
        }
        solver = TwoDimParabolicSolver(
            rx = PIF / 4f,
            ry = ln(2f),
            a = 10f,
            N = N,
            M = M,
            K = K,
            T = T,
            phi1 = { y, t -> cosh(y) * exp(-3f * a * t) },
            phi3 = { x, t -> cos(2f * x) * exp(-3f * a * t) },
            phi4 = { x, t -> 5f * cos(2f * x) * exp(-3f * a * t) / 4f },
            ksi = { x, y -> cos(2f * x) * cosh(y) },
            analyticalSolution = { x, y, t -> cos(2f * x) * cosh(y) * exp(-3f * a * t) }
        )
        analyticalSolution = solver.actualFunction
        isChanged = false
    }

    private fun initSliders() {
        with(binding) {
            xSlider.valueFrom = 0f
            xSlider.valueTo = PIF / 4f
            xSlider.stepSize = (PIF / 4f - 0f) / N
            xSlider.value = 0f
            ySlider.valueFrom = 0f
            ySlider.valueTo = ln(2f)
            ySlider.stepSize = ln(2f) / M
            ySlider.value = 0f
        }
        xIndex = 0
        yIndex = 0
    }
}