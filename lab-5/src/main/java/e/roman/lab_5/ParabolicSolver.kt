package e.roman.lab_5

import android.util.Log
import e.roman.mathematics.PIF
import e.roman.mathematics.SystemSolver
import e.roman.mathematics.Table2DFunctionSnapshot
import e.roman.mathematics.preLast
import java.lang.Exception
import kotlin.math.*

class ParabolicSolver {

    companion object {

        fun explicitFiniteDifferenceSolver(
            l: Float,
            time: Float,
            xCount: Int,
            tCount: Int,
            phi0: ((t: Float) -> Float),
            phi1: ((t: Float) -> Float),
            psi: ((x: Float) -> Float),
            a: Float,
            freeMember: ((x: Float, t: Float) -> Float)
        ): List<List<Table2DFunctionSnapshot>> {
            val h = l / xCount
            val tau = time / tCount
            var x: Float
            val sigma = a * tau / h.pow(2)
            Log.d("checkk", "$tau / $h ^ 2 = $sigma")
            val u = mutableListOf<MutableList<Table2DFunctionSnapshot>>()
            u += mutableListOf<Table2DFunctionSnapshot>()
            var t = 0f
            for (i in 0..xCount)
                u.first() += Table2DFunctionSnapshot(0f, t, psi(0f))
            t += tau
            for (i in 0 until tCount) {
                x = h
                u += mutableListOf<Table2DFunctionSnapshot>()
                u.last() += Table2DFunctionSnapshot(0f, t, phi0(t))
                for (j in 1 until xCount) {
                    var value = sigma * u.preLast()[j + 1].value
                    value += (1 - 2 * sigma) * u.preLast()[j].value
                    value += sigma * u.preLast()[j - 1].value
                    value += tau * freeMember(x, t)
                    u.last() += Table2DFunctionSnapshot(x, t, value)
                    x += h
                }
                u.last() += Table2DFunctionSnapshot(x, t, phi1(t))
                t += tau
            }
            return u
        }

        fun implicitFiniteDifferenceSolver(
            l: Float,
            time: Float,
            xCount: Int,
            tCount: Int,
            phi0: ((t: Float) -> Float),
            phi1: ((t: Float) -> Float),
            psi: ((x: Float) -> Float),
            a: Float,
            freeMember: ((x: Float, t: Float) -> Float)
        ): List<List<Table2DFunctionSnapshot>> =
            weightedSolution(l, time, xCount, tCount, phi0, phi1, psi, a, freeMember, 1f)

        fun crankNicholsonSolver(
            l: Float,
            time: Float,
            xCount: Int,
            tCount: Int,
            phi0: ((t: Float) -> Float),
            phi1: ((t: Float) -> Float),
            psi: ((x: Float) -> Float),
            a: Float,
            freeMember: ((x: Float, t: Float) -> Float)
        ): List<List<Table2DFunctionSnapshot>> =
            weightedSolution(l, time, xCount, tCount, phi0, phi1, psi, a, freeMember, 0.5f)

        private fun weightedSolution(
            l: Float,
            time: Float,
            xCount: Int,
            tCount: Int,
            phi0: ((t: Float) -> Float),
            phi1: ((t: Float) -> Float),
            psi: ((x: Float) -> Float),
            a: Float,
            freeMember: ((x: Float, t: Float) -> Float),
            theta: Float
        ): List<List<Table2DFunctionSnapshot>> {
            if (theta < 0f || theta > 1f)
                throw Exception("theta must be in [0; 1]")
            val h = l / xCount
            val tau = time / tCount
            var x: Float
            val sigma = a * tau / h.pow(2)
            val u = mutableListOf<MutableList<Table2DFunctionSnapshot>>()
            u += mutableListOf<Table2DFunctionSnapshot>()
            var t = 0f
            x = 0f
            for (i in 0..xCount) {
                u.first() += Table2DFunctionSnapshot(x, 0f, psi(x))
                x += h
            }
            t += tau
            for (i in 0 until tCount) {
                x = h
                u += mutableListOf<Table2DFunctionSnapshot>()
                u.last() += Table2DFunctionSnapshot(0f, t, phi0(t))
                val b = -(1f + 2f * sigma * theta)
                var d = -(u.preLast()[1].value + (1f - theta) * sigma * (u.preLast()[2].value - 2f * u.preLast()[1].value + u.preLast()[0].value) + sigma * phi0(t) + tau * freeMember(x, t))
                val aSystem = mutableListOf(0f)
                val bSystem = mutableListOf(b)
                val cSystem = mutableListOf(sigma * theta)
                val dSystem = mutableListOf(d)
                x += h
                for (j in 2 until xCount - 1) {
                    d = -(u.preLast()[j].value + (1f - theta) * sigma * (u.preLast()[j + 1].value - 2f * u.preLast()[j].value + u.preLast()[j - 1].value) + tau * freeMember(x, t))
                    aSystem += sigma * theta
                    bSystem += b
                    cSystem += sigma * theta
                    dSystem += d
                    x += h
                }
                d = -(u.preLast()[1].value + (1f - theta) * sigma * (u.preLast()[2].value - 2f * u.preLast()[1].value + u.preLast()[0].value) + sigma * phi1(t) + tau * freeMember(x, t))
                aSystem += sigma * theta
                bSystem += b
                cSystem += 0f
                dSystem += d
                val res = SystemSolver.triDiagonalSolve(aSystem, bSystem, cSystem, dSystem)
                x = h
                for (value in res) {
                    u.last() += Table2DFunctionSnapshot(x, t, value)
                    x += h
                }
                u.last() += Table2DFunctionSnapshot(x, t, phi1(t))
                t += tau
            }
            return u
        }

        fun actualFunction(x: Float, t: Float) =
            (1 - exp(-PIF.pow(2) * t)) * sin(PIF * x) / PIF.pow(2)
    }
}