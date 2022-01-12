package e.roman.lab_5

import e.roman.mathematics.ThreeDimArray
import kotlin.math.pow

class TwoDimParabolicSolver(
    private val lx: Float = 0f,
    rx: Float = 1f,
    private val ly: Float = 0f,
    ry: Float = 1f,
    private val N: Int = 10,
    private val M: Int = 10,
    private val K: Int = 10,
    T: Float = 1f,
    private val a: Float = 1f,
    private val phi1: ((Float, Float) -> Float) = { _, _ -> 0f },
    private val phi2: ((Float, Float) -> Float) = { _, _ -> 0f },
    private val phi3: ((Float, Float) -> Float) = { _, _ -> 0f },
    private val phi4: ((Float, Float) -> Float) = { _, _ -> 0f },
    private val ksi: ((Float, Float) -> Float) = { _, _ -> 0f },
    private val analyticalSolution: ((Float, Float, Float) -> Float) = { _, _, _ -> 0f }
) {

    private val tau = T / K
    private val h1 = (rx - lx) / N
    private val h2 = (ry - ly) / M

    val actualFunction by lazy { answerAnalyticalSolution() }
    val alternatingDirectionsSolution by lazy { alternatingDirections() }
    val fractionalStepsSolution by lazy { fractionalSteps() }

    private fun answerAnalyticalSolution(): ThreeDimArray {
        val u: MutableList<List<MutableList<Float>>> = mutableListOf()
        for (k in 0..K) {
            val tmp: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0..N)
                tmp += MutableList(M + 1) { 0f }
            u += tmp
        }
        var x: Float
        var y: Float
        var t: Float
        for (k in 0..K) {
            t = k * tau
            for (i in 0..N) {
                x = lx + i * h1
                for (j in 0..M) {
                    y = ly + j * h2
                    u[k][i][j] = analyticalSolution(x, y, t)
                }
            }
        }
        return u.toCorrectAnswer()
    }

    private fun triDiagonalAlgo(
        a: List<Float>,
        b: List<Float>,
        c: List<Float>,
        d: List<Float>
    ): List<Float> {
        val n = a.size
        val p: MutableList<Float> = MutableList(n + 1) { 0f }
        val q: MutableList<Float> = MutableList(n + 1) { 0f }
        for (i in 0 until n) {
            p[i + 1] = -1 * c[i] / (b[i] + a[i] * p[i])
            q[i + 1] = (d[i] - a[i] * q[i]) / (b[i] + a[i] * p[i])
        }
        val x: MutableList<Float> = MutableList(n) { 0f }
        for (i in n - 1 downTo 0)
            if (i == n - 1)
                x[i] = q[n]
            else
                x[i] = q[i + 1] + p[i + 1] * x[i + 1]
        return x
    }

    private fun alternatingDirections(): ThreeDimArray {
        val u: MutableList<List<MutableList<Float>>> = mutableListOf()
        for (k in 0..K) {
            val tmp: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0..N)
                tmp += MutableList(M + 1) { 0f }
            u += tmp
        }

        // t = 0
        for (i in 0..N) {
            val x = lx + i * h1
            for (j in 0..M) {
                val y = ly + j * h2
                u[0][i][j] = ksi(x, y)
            }
        }
        for (k in 0 until K) {
            val t = (k + 1) * tau
            val kHalf: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0..N)
                kHalf += MutableList(M + 1) { 0f }
            for (j in 1 until M) {
                val y = ly + j * h2
                val aCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val bCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val cCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val dCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                for (i in 1 until N) {
                    aCoefficients[i] = a / h1.pow(2)
                    bCoefficients[i] = -2f * a / h1.pow(2) - 2f / tau
                    cCoefficients[i] = a / h1.pow(2)
                    dCoefficients[i] = (a / h1.pow(2) * (-1f * u[k][i][j + 1]
                            + 2 * u[k][i][j] - u[k][i][j - 1])
                            - 2f / tau * u[k][i][j])
                }
                aCoefficients[0] = 0f
                bCoefficients[0] = 1f
                cCoefficients[0] = 0f
                dCoefficients[0] = phi1(y, t - tau / 2f)
                aCoefficients[N] = 0f
                bCoefficients[N] = 1f
                cCoefficients[N] = 0f
                dCoefficients[N] = phi2(y, t - tau / 2f)
                val ans =
                    triDiagonalAlgo(aCoefficients, bCoefficients, cCoefficients, dCoefficients)
                for (i in 0..N)
                    kHalf[i][j] = ans[i]
            }
            for (i in 0..N) {
                val x = lx + i * h1
                kHalf[i][0] = phi3(x, t - tau / 2f)
                kHalf[i][M] = phi4(x, t - tau / 2f)
            }
            for (i in 1 until N) {
                val x = lx + i * h1
                val aCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val bCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val cCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val dCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                for (j in 1 until M) {
                    aCoefficients[j] = a / h2.pow(2)
                    bCoefficients[j] = -2f * a / h2.pow(2) - 2f / tau
                    cCoefficients[j] = a / h2.pow(2)
                    dCoefficients[j] = (a / h1.pow(2)
                            * (-1f * kHalf[i + 1][j] + 2 * kHalf[i][j] - kHalf[i - 1][j])
                            - 2f / tau * kHalf[i][j])
                }
                aCoefficients[0] = 0f
                bCoefficients[0] = 1f
                cCoefficients[0] = 0f
                dCoefficients[0] = phi3(x, t)
                aCoefficients[M] = 0f
                bCoefficients[M] = 1f
                cCoefficients[M] = 0f
                dCoefficients[M] = phi4(x, t)
                val ans =
                    triDiagonalAlgo(aCoefficients, bCoefficients, cCoefficients, dCoefficients)
                for (j in 0..M)
                    u[k + 1][i][j] = ans[j]
            }
            for (j in 0..M) {
                val y = ly + j * h2
                u[k + 1][0][j] = phi1(y, t)
                u[k + 1][N][j] = phi2(y, t)
            }
        }
        return u.toCorrectAnswer()
    }

    private fun fractionalSteps(): ThreeDimArray {
        val u: MutableList<List<MutableList<Float>>> = mutableListOf()
        for (k in 0..K) {
            val tmp: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0..N)
                tmp += MutableList(M + 1) { 0f }
            u += tmp
        }

        // t = 0
        for (i in 0..N) {
            val x = lx + i * h1
            for (j in 0..M) {
                val y = ly + j * h2
                u[0][i][j] = ksi(x, y)
            }
        }
        for (k in 0 until K) {
            val t = (k + 1) * tau
            val kHalf: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0..N) {
                kHalf += MutableList(M + 1) { 0f }
            }
            for (j in 1 until M) {
                val y = ly + j * h2
                val aCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val bCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val cCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                val dCoefficients: MutableList<Float> = MutableList(N + 1) { 0f }
                for (i in 1 until N) {
                    aCoefficients[i] = a / h1.pow(2)
                    bCoefficients[i] = -2f * a / h1.pow(2) - 1 / tau
                    cCoefficients[i] = a / h1.pow(2)
                    dCoefficients[i] = u[k][i][j] * -1f / tau
                }
                aCoefficients[0] = 0f
                bCoefficients[0] = 1f
                cCoefficients[0] = 0f
                dCoefficients[0] = phi1(y, t - tau / 2f)
                aCoefficients[N] = 0f
                bCoefficients[N] = 1f
                cCoefficients[N] = 0f
                dCoefficients[N] = phi2(y, t - tau / 2f)
                val ans =
                    triDiagonalAlgo(aCoefficients, bCoefficients, cCoefficients, dCoefficients)
                for (i in 0..N) {
                    kHalf[i][j] = ans[i]
                }
            }
            for (i in 0..N) {
                val x = lx + i * h1
                kHalf[i][0] = phi3(x, t - tau / 2f)
                kHalf[i][M] = phi4(x, t - tau / 2f)
            }
            for (i in 1 until N) {
                val x = lx + i * h1
                val aCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val bCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val cCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                val dCoefficients: MutableList<Float> = MutableList(M + 1) { 0f }
                for (j in 1 until M) {
                    aCoefficients[j] = a / h2.pow(2)
                    bCoefficients[j] = -2f * a / h2.pow(2) - 1f / tau
                    cCoefficients[j] = a / h2.pow(2)
                    dCoefficients[j] = kHalf[i][j] * -1f / tau
                }
                aCoefficients[0] = 0f
                bCoefficients[0] = 1f
                cCoefficients[0] = 0f
                dCoefficients[0] = phi3(x, t)
                aCoefficients[M] = 0f
                bCoefficients[M] = 1f
                cCoefficients[M] = 0f
                dCoefficients[M] = phi4(x, t)
                val ans =
                    triDiagonalAlgo(aCoefficients, bCoefficients, cCoefficients, dCoefficients)
                for (j in 0..M) {
                    u[k + 1][i][j] = ans[j]
                }
            }
            for (j in 0..M) {
                val y = ly + j * h2
                u[k + 1][0][j] = phi1(y, t)
                u[k + 1][N][j] = phi2(y, t)
            }
        }
        return u.toCorrectAnswer()
    }

    private fun List<List<List<Float>>>.toCorrectAnswer(): ThreeDimArray {
        var x = lx
        val result =
            mutableListOf<Pair<Float, MutableList<Pair<Float, MutableList<Pair<Float, Float>>>>>>()
        for (i in 0 until N) {
            var y = ly
            result += x to mutableListOf()
            for (j in 0 until M) {
                result[i].second += y to mutableListOf()
                var t = 0f
                for (k in 0 until K) {
                    result[i].second[j].second += t to this[k][i][j]
                    t += tau
                }
                y += h2
            }
            x += h1
        }
        return result
    }
}
