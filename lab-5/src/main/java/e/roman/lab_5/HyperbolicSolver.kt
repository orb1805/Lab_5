package e.roman.lab_5

import e.roman.mathematics.SystemSolver
import e.roman.mathematics.Table2DFunctionSnapshot
import e.roman.mathematics.preLast
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

class HyperbolicSolver(
    l: Float = 1f,
    time: Float = 1f,
    private val xCount: Int = 10,
    private val tCount: Int = 10,
    private val phi0: ((t: Float) -> Float) = { 0f },
    private val phi1: ((t: Float) -> Float) = { 0f },
    private val psi1: ((x: Float) -> Float) = { 0f },
    private val psi2: ((x: Float) -> Float) = { 0f },
    private val dPsi1: ((x: Float) -> Float) = { 0f },
    private val ddPsi1: ((x: Float) -> Float) = { 0f },
    private val a: Float = 0f,
    private val b: Float = 0f,
    private val c: Float = 0f,
    private val f: ((x: Float, t: Float) -> Float) = { _, _ -> 0f },
) {

    val explicitSolution: List<List<Table2DFunctionSnapshot>> by lazy { solveExplicit() }
    val implicitSolution: List<List<Table2DFunctionSnapshot>> by lazy {
        solveImplicit(
            psi1,
            { x, t -> psi1(x) + psi2(x) * tau + (a * ddPsi1(x) + b * dPsi1(x) + c * psi1(x) + f(x, t) * tau.pow(2) / 2f) }
        )
    }

    private val h = l / xCount
    private val tau = time / tCount

    private fun solveExplicit(): List<List<Table2DFunctionSnapshot>> {
        val u = mutableListOf(mutableListOf<Table2DFunctionSnapshot>())
        var x = 0f
        var t = 0f
        for (i in 0..xCount) {
            u.first() += Table2DFunctionSnapshot(0f, t, psi1(x))
            x += h
        }
        t += tau
        u += mutableListOf<Table2DFunctionSnapshot>()
        x = 0f
        for (i in 0..xCount) {
            u.last() += Table2DFunctionSnapshot(x, t, psi1(x) + psi2(x) * tau)
            x += h
        }
        t += tau
        val sigma = a * (tau / h).pow(2)
        val gamma = b * tau.pow(2) / (2f * h)
        for (i in 1 until tCount) {
            x = h
            u += mutableListOf<Table2DFunctionSnapshot>()
            u.last() += Table2DFunctionSnapshot(0f, t, phi0(t) * h)
            for (j in 1 until xCount) {
                var value = u.preLast()[j + 1].value * (sigma + gamma)
                value += u.preLast()[j].value * (-2f * sigma + 2f + c * tau.pow(2))
                value += u.preLast()[j - 1].value * (sigma - gamma)
                value -= u[u.lastIndex - 2][j].value
                value += tau.pow(2) * f(x, t)
                u.last() += Table2DFunctionSnapshot(x, t, value)
                x += h
            }
            u.last() += Table2DFunctionSnapshot(x, t, phi1(t) * h)
            t += tau
        }
        return u
    }

    private fun solveImplicit(
        x1: ((x: Float) -> Float),
        x2: ((x: Float, t: Float) -> Float)
    ): List<List<Table2DFunctionSnapshot>> {
        val u = mutableListOf(mutableListOf<Table2DFunctionSnapshot>())
        var x = 0f
        var t = 0f
        for (i in 0..xCount) {
            u.first() += Table2DFunctionSnapshot(x, t, x1(x))
            x += h
        }
        t += tau
        u += mutableListOf<Table2DFunctionSnapshot>()
        x = 0f
        for (i in 0..xCount) {
            u.last() += Table2DFunctionSnapshot(x, t, x2(x, t))
            x += h
        }
        t += tau
        val sigma = a * (tau / h).pow(2)
        val gamma = b * tau.pow(2) / (2f * h)
        for (i in 1..tCount - 2) {
            x = 0f
            u += mutableListOf<Table2DFunctionSnapshot>()
            val b = 1f + 2f * sigma - c * tau.pow(2)
            var d = phi0(t)
            val aSystem = mutableListOf(0f)
            val bSystem = mutableListOf(b)
            val cSystem = mutableListOf(-sigma + gamma)
            val dSystem = mutableListOf(d)
            x += h
            for (j in 1 until xCount) {
                d = 2f * u.preLast()[j].value - u[u.lastIndex - 2][j].value + tau.pow(2) * f(x, t)
                aSystem += -sigma - gamma
                bSystem += b
                cSystem += -sigma + gamma
                dSystem += d
                x += h
            }
            d = phi1(t)
            aSystem += -sigma - gamma
            bSystem += b
            cSystem += 0f
            dSystem += d
            val systemSolution = SystemSolver.triDiagonalSolve(aSystem, bSystem, cSystem, dSystem)
            x = 0f
            systemSolution.forEach { value ->
                u.last() += Table2DFunctionSnapshot(x, t, value)
                x += h
            }
            t += tau
        }
        return u
    }

    companion object {

        fun actualFunction(x: Float, t: Float): Float =
            0.5f * exp(-x) * sin(x) * sin(2f * t)
    }
}