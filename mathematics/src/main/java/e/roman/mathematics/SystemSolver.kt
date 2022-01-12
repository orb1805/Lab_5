package e.roman.mathematics

import java.lang.Exception

class SystemSolver {

    companion object {

        fun triDiagonalSolve(a: List<Float>, b: List<Float>, c: List<Float>, d: List<Float>): MutableList<Float> {
            if (a.size != b.size || b.size != c.size || c.size != d.size)
                throw Exception("a, b, c and d sizes must be equal")
            val x = mutableListOf<Float>()
            val p = mutableListOf<Float>()
            val q = mutableListOf<Float>()
            p.add(-c[0] / b[0])
            q.add(d[0] / b[0])
            for (i in 1 .. a.lastIndex){
                p.add(-c[i] / (b[i] + a[i] * p[i - 1]))
                q.add((d[i] - a[i] * q[i - 1]) / (b[i] + a[i] * p[i - 1]))
            }
            x.add(q.last())
            for (i in a.lastIndex - 1 downTo 0)
                x.add(p[i] * x.last() + q[i])
            x.reverse()
            return x
        }

        fun luSolve(a: Matrix, b: MutableList<Float>): MutableList<Float> {
            var l = Matrix(a.data.size, 1f)
            val u = Matrix.copy(a) //Matrix(a.data.size, a.data.size, 0f)
            var m: Matrix
            for (i in a.data.indices) {
                m = Matrix(a.data.size, 1f)
                for (j in i + 1 until a.data.size) {
                    m.data[j][i] = u.data[j][i] / u.data[i][i]
                    for (k in a.data.indices) {
                        u.data[j][k] -= m.data[j][i] * u.data[i][k]
                    }
                }
                l = Matrix.multiply(l, m)
            }
            val z = solveZ(b, l)
            return solveX(z, u)
        }

        private fun solveX(z: MutableList<Float>, u: Matrix): MutableList<Float> {
            val x = mutableListOf<Float>()
            for (i in z.indices)
                x.add(0f)
            x[x.lastIndex] = z.last() / u.data.last().last()
            for (i in x.lastIndex - 1 downTo 0) {
                x[i] = z[i]
                for (j in i + 1..x.lastIndex)
                    x[i] -= u.data[i][j] * x[j]
                x[i] /= u.data[i][i]
            }
            return x
        }

        private fun solveZ(b: MutableList<Float>, l: Matrix): MutableList<Float> {
            val z = mutableListOf<Float>()
            z.add(b[0])
            for (i in 1 until b.size) {
                z.add(b[i])
                for (j in 0 until i)
                    z[z.lastIndex] -= l.data[i][j] * z[j]
            }
            return z
        }
    }
}