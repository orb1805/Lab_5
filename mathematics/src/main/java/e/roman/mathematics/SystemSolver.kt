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
    }
}