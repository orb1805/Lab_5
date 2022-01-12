package e.roman.lab_5

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Float.max
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import e.roman.mathematics.Table2DFunctionSnapshot
import kotlin.math.exp


class EllipticSolver(
    private val l: Float,
    private val rx: Float,
    private val lx: Float,
    private val ry: Float,
    private val ly: Float,
    private val N1: Int,
    private val N2: Int,
    private val f1: ((Float) -> Float) = { 0f },
    private val f2: ((Float) -> Float) = { 0f },
    private val f3: ((Float) -> Float) = { 0f },
    private val f4: ((Float) -> Float) = { 0f }
) {

    private fun initialization(): List<MutableList<Float>> {
        val h1: Float = (rx - lx) / N1
        val h2: Float = (ry - ly) / N2
        val u: MutableList<MutableList<Float>> = ArrayList(N1 + 1)
        for (i in 0..N1) {
            u.add(ArrayList(Collections.nCopies(N2 + 1, 0.0f)))
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            u[0][j] = cos(y)
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            u[N1][j] = (Math.E * cos(y)).toFloat()
        }
        for (i in 1 until N1) {
            val x: Float = i * h1 + lx
            for (j in 1 until N2) {
                val y: Float = j * h2 + ly
                u[i][j] =
                    (f2(y) - f1(y)) / (rx - lx) * (x - lx)
            }
        }
        for (i in 1 until N1) {
            val x: Float = i * h1 + lx
            u[i][0] = u[i][1] - h2 * f3(x)
        }
        for (i in 1 until N1) {
            val x: Float = i * h1 + lx
            u[i][N2] = u[i][N2 - 1] + h2 * f4(x)
        }
        return u
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun norm(ukPlus1: List<MutableList<Float>>, uk: List<MutableList<Float>>): Float {
        var res = -1.0f
        for (i in 0..N1) {
            for (j in 0..N2) {
                res = max(abs(ukPlus1[i][j] - uk[i][j]), res)
            }
        }
        return res
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun norm2(ukPlus1: List<Float>, uk: List<Float>): Float {
        var res = -1.0f
        for (i in 0..N1) {
            for (j in 0..N2) {
                res = max(abs(ukPlus1[matrixToVectorIndices(i, j)] - uk[matrixToVectorIndices(i, j)]), res)
            }
        }
        return res
    }

    private fun copy(u: MutableList<MutableList<Float>>): List<MutableList<Float>> {
        val n = u.size
        val res: MutableList<MutableList<Float>> = ArrayList(n)
        for (i in 0 until n) {
            val row: MutableList<Float> = ArrayList(u[i])
            res.add(row)
        }
        return res
    }

    private fun copy2(u: List<Float>): MutableList<Float> {
        return ArrayList(u)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun liebmann(eps: Float): List<List<Table2DFunctionSnapshot>> {
        var interCount = 0
        val h1: Float = (rx - lx) / N1
        val h2: Float = (ry - ly) / N2
        var uPrev = initialization()
        val uCur: MutableList<MutableList<Float>> = ArrayList(N1 + 1)
        for (i in 0..N1) {
            uCur.add(ArrayList(Collections.nCopies(N2 + 1, 0.0f)))
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            uCur[0][j] = cos(y)
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            uCur[N1][j] = (Math.E * cos(y)).toFloat()
        }
        var k = 0
        while (true) {
            interCount++
            for (i in 1 until N1) {
                for (j in 1 until N2) {
                    uCur[i][j] = ((h2.pow(2) * uPrev[i + 1][j] + h2.pow(2.0f) * uPrev[i - 1][j] + h1.pow(
                        2.0f
                    ) * uPrev[i][j + 1] + h1.pow(2.0f) * uPrev[i][j - 1]) / 2 / (h1.pow(2.0f) + h2.pow(
                        2.0f
                    )))
                }
            }
            for (i in 1 until N1) {
                val x: Float = i * h1 + lx
                uCur[i][0] = uCur[i][1] - h2 * f3(x)
            }
            for (i in 1 until N1) {
                val x: Float = i * h1 + lx
                uCur[i][N2] = uCur[i][N2 - 1] + h2 * f4(x)
            }
            if (norm(uCur, uPrev) <= eps) {
                ++k
                break
            }
            uPrev = copy(uCur)
            ++k
        }
        val answer = Answer(lx, rx, ly, ry, h1, h2, k + 1, uCur)
        Log.d("checkk", interCount.toString())
        return answer.toTable2DSnapshot()
    }

    private fun matrixToVectorIndices(i: Int, j: Int): Int {
        return i + j * (N1 + 1)
    }

    private fun reverseMapCords(c: Int): IntArray {
        return intArrayOf(c % (N1 + 1), c / (N1 + 1))
    }

    private fun multMatrixVec(alpha: List<MutableList<Float>>, u: List<Float>): List<Float> {
        val res: MutableList<Float> = ArrayList(Collections.nCopies(u.size, 0.0f))
        for (i in alpha.indices) {
            for (j in alpha[i].indices) {
                res[i] = res[i] + alpha[i][j] * u[j]
            }
        }
        return res
    }

    private fun sumVec(vec1: List<Float>, vec2: List<Float>): List<Float> {
        val res: MutableList<Float> = ArrayList(vec1)
        for (i in vec1.indices) {
            res[i] = res[i] + vec2[i]
        }
        return res
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun seidel(eps: Float): List<List<Table2DFunctionSnapshot>> {
        var interCount = 0
        val h1: Float = (rx - lx) / N1
        val h2: Float = (ry - ly) / N2
        val uInit = initialization()
        var uPrev: MutableList<Float> = ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f))
        var uCur: List<Float>
        for (i in 0..N1) {
            for (j in 0..N2) {
                uPrev[matrixToVectorIndices(i, j)] = uInit[i][j]
            }
        }
        val beta: MutableList<Float> = ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f))
        val alpha: MutableList<MutableList<Float>> = ArrayList((N1 + 1) * (N2 + 1))
        for (i in 0 until (N1 + 1) * (N2 + 1)) {
            alpha.add(ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f)))
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            beta[matrixToVectorIndices(0, j)] = f1(y)
            beta[matrixToVectorIndices(N1, j)] = f2(y)
        }
        for (i in 1 until N1) {
            val x: Float = i * h1 + lx
            beta[matrixToVectorIndices(i, 0)] = -1.0f * h2 * f3(x)
            beta[matrixToVectorIndices(i, N2)] = h2 * f4(x)
            alpha[matrixToVectorIndices(i, 0)][matrixToVectorIndices(i, 1)] = 1.0f
            alpha[matrixToVectorIndices(i, N2)][matrixToVectorIndices(i, N2 - 1)] = 1.0f
        }
        for (i in 1 until N1) {
            for (j in 1 until N2) {
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i + 1, j)] =
                    (h2.pow(2) / 2.0 / (h1.pow(2) + h2.pow(2))).toFloat()
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i - 1, j)] =
                    (h2.pow(2) / 2.0 / (h1.pow(2) + h2.pow(2))).toFloat()
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i, j + 1)] =
                    (h1.pow(2) / 2.0 / (h1.pow(2) + h2.pow(2))).toFloat()
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i, j - 1)] =
                    (h1.pow(2) / 2.0 / (h1.pow(2) + h2.pow(2))).toFloat()
            }
        }
        var k = 0
        while (true) {
            interCount++
            val tmpU = copy2(uPrev)
            for (q in tmpU.indices) {
                val newU = multMatrixVec(alpha, tmpU)
                tmpU[q] = sumVec(newU, beta)[q]
            }
            uCur = tmpU
            if (norm2(uCur, uPrev) <= eps) {
                ++k
                break
            }
            uPrev = copy2(uCur)
            ++k
        }
        val u: MutableList<MutableList<Float>> = ArrayList(N1 + 1)
        for (i in 0..N1) {
            u.add(ArrayList(Collections.nCopies(N2 + 1, 0.0f)))
        }
        for (q in uCur.indices) {
            val i = reverseMapCords(q)[0]
            val j = reverseMapCords(q)[1]
            u[i][j] = uCur[q]
        }
        val answer = Answer(lx, rx, ly, ry, h1, h2, k + 1, u)
        Log.d("checkk", interCount.toString())
        return answer.toTable2DSnapshot()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun relaxation(eps: Float, tau: Float): List<List<Table2DFunctionSnapshot>> {
        var interCount = 0
        val h1: Float = (rx - lx) / N1
        val h2: Float = (ry - ly) / N2
        val uInit = initialization()
        var uPrev: MutableList<Float> = ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f))
        var uCur: List<Float>
        for (i in 0..N1) {
            for (j in 0..N2) {
                uPrev[matrixToVectorIndices(i, j)] = uInit[i][j]
            }
        }
        val beta: MutableList<Float> = ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f))
        val alpha: MutableList<MutableList<Float>> = ArrayList((N1 + 1) * (N2 + 1))
        for (i in 0 until (N1 + 1) * (N2 + 1)) {
            alpha.add(ArrayList(Collections.nCopies((N1 + 1) * (N2 + 1), 0.0f)))
        }
        for (j in 0..N2) {
            val y: Float = j * h2 + ly
            beta[matrixToVectorIndices(0, j)] = f1(y)
            beta[matrixToVectorIndices(N1, j)] = f2(y)
        }
        for (i in 1 until N1) {
            val x: Float = i * h1 + lx
            beta[matrixToVectorIndices(i, 0)] = -1.0f * h2 * f3(x)
            beta[matrixToVectorIndices(i, N2)] = h2 * f4(x)
            alpha[matrixToVectorIndices(i, 0)][matrixToVectorIndices(i, 1)] = 1.0f
            alpha[matrixToVectorIndices(i, N2)][matrixToVectorIndices(i, N2 - 1)] = 1.0f
        }
        for (i in 1 until N1) {
            for (j in 1 until N2) {
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i + 1, j)] =
                    (tau * h2.pow(2) / 2.0f / (h1.pow(2) + h2.pow(2)))
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i - 1, j)] =
                    (tau * h2.pow(2) / 2.0f / (h1.pow(2) + h2.pow(2)))
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i, j + 1)] =
                    (tau * h1.pow(2) / 2.0f / (h1.pow(2) + h2.pow(2)))
                alpha[matrixToVectorIndices(i, j)][matrixToVectorIndices(i, j - 1)] =
                    (tau * h1.pow(2) / 2.0f / (h1.pow(2) + h2.pow(2)))
            }
        }
        var k = 0
        while (true) {
            interCount++
            val tmpU = copy2(uPrev)
            for (q in tmpU.indices) {
                val cords = reverseMapCords(q)
                if (cords[0] >= 1 && cords[0] <= N1 - 1 && cords[1] >= 1 && cords[1] <= N2 - 1) {
                    beta[q] = (1 - tau) * tmpU[q]
                }
            }
            for (q in tmpU.indices) {
                val newU = multMatrixVec(alpha, tmpU)
                tmpU[q] = sumVec(newU, beta)[q]
            }
            uCur = tmpU
            if (norm2(uCur, uPrev) <= eps) {
                ++k
                break
            }
            uPrev = copy2(uCur)
            ++k
        }
        val u: MutableList<MutableList<Float>> = ArrayList(N1 + 1)
        for (i in 0..N1) {
            u.add(ArrayList(Collections.nCopies(N2 + 1, 0.0f)))
        }
        for (q in uCur.indices) {
            val i = reverseMapCords(q)[0]
            val j = reverseMapCords(q)[1]
            u[i][j] = uCur[q]
        }
        val answer = Answer(lx, rx, ly, ry, h1, h2, k + 1, u)
        Log.d("checkk", interCount.toString())
        return answer.toTable2DSnapshot()
    }

    data class Answer(
        private val lx: Float,
        private val rx: Float,
        private val ly: Float,
        private val ry: Float,
        private val h1: Float,
        private val h2: Float,
        private val cntIter: Int,
        private val u: List<MutableList<Float>>
    ) {

        fun toTable2DSnapshot(): List<List<Table2DFunctionSnapshot>> {
            var i: Int
            var j = 0
            var x: Float
            var y = ly
            val result = mutableListOf<MutableList<Table2DFunctionSnapshot>>()
            while (y < ry) {
                i = 0
                x = 0f
                result += mutableListOf<Table2DFunctionSnapshot>()
                while (x < rx) {
                    result.last() += Table2DFunctionSnapshot(x, y, u[i][j])
                    x += h1
                    i++
                }
                y += h2
                j++
            }
            return result
        }
    }

    companion object {

        fun actualFunction(x: Float, y: Float): Float =
            exp(x) * cos(y)
    }
}