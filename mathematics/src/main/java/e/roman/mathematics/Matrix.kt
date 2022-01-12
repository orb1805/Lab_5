package e.roman.mathematics

import kotlin.math.abs
import kotlin.math.sqrt

class Matrix {

    val data: MutableList<MutableList<Float>>

    constructor(data1: MutableList<MutableList<Float>>){
        data = data1
    }

    constructor(rows: Int, columns: Int, value: Float = 0f){
        data = mutableListOf()
        for (i in 1 .. rows) {
            data.add(mutableListOf())
            for (j in 1 .. columns)
                data.last().add(value)
        }
    }

    constructor(size: Int, diagonalValue: Float = 1f){
        data = mutableListOf()
        for (i in 1 .. size) {
            data.add(mutableListOf())
            for (j in 1 .. size)
                data.last().add(0f)
        }
        for (i in 0 until size)
            data[i][i] = diagonalValue
    }

    fun changeRows(n1: Int, n2: Int) {
        for (i in data[0].indices){
            data[n1][i] += data[n2][i]
            data[n2][i] = data[n1][i] - data[n2][i]
            data[n1][i] -= data[n2][i]
        }
    }

    fun infNorm(): Float {
        var max = 0f
        var sum: Float
        for (i in data.indices){
            sum = 0f
            for (j in data[0].indices)
                sum += abs(data[i][j])
            if (sum > max)
                max = sum
        }
        return max
    }

    fun symmetricalNorm(): Float {
        var res = 0f
        for (i in data.indices)
            for (j in i + 1 .. data[i].lastIndex)
                res += data[i][j] * data[i][j]
        return sqrt(res)
    }

    fun triangleNorm(): Float {
        var res = abs(data[1][0])
        for (i in 0 until data.lastIndex) {
            if (abs(data[i + 1][i]) < res)
                res = abs(data[i + 1][i])
        }
        return res
    }

    override fun toString(): String {
        var string = "size ${data.size} x ${data[0].size}\n"
        for (i in data.indices) {
            for (j in data[i].indices)
                string += data[i][j].toString() + " "
            if (data[i].isEmpty())
                string += "--"
            string += "\n"
        }
        return string
    }

    companion object {

        fun multiply(a: Matrix, b: Matrix): Matrix {
            val result = Matrix(a.data.size, b.data[0].size, 0f)
            for (i in a.data.indices) {
                for (j in b.data[0].indices) {
                    for (k in a.data[0].indices)
                        result.data[i][j] += a.data[i][k] * b.data[k][j]
                }
            }
            return result
        }

        fun multiply(v: Float, b: Matrix): Matrix {
            val result = Matrix(b.data.size, b.data[0].size, 1f)
            for (i in b.data.indices)
                for (j in b.data[i].indices)
                    result.data[i][j] = b.data[i][j] * v
            return result
        }

        fun sum(a: Matrix, b: Matrix): Matrix {
            val result = mutableListOf<MutableList<Float>>()
            for (i in a.data.indices) {
                result.add(mutableListOf())
                for (j in a.data[i].indices)
                    result.last().add(a.data[i][j] + b.data[i][j])
            }
            return Matrix(result)
        }

        fun difference(a: Matrix, b: Matrix): Matrix {
            val result = mutableListOf<MutableList<Float>>()
            for (i in a.data.indices) {
                result.add(mutableListOf())
                for (j in a.data[i].indices)
                    result.last().add(a.data[i][j] - b.data[i][j])
            }
            return Matrix(result)
        }

        fun copy(a: Matrix): Matrix {
            val result = mutableListOf<MutableList<Float>>()
            for (i in a.data) {
                result.add(mutableListOf())
                for (j in i)
                    result.last().add(j)
            }
            return Matrix(result)
        }

        fun transpose(a: Matrix): Matrix {
            val result = Matrix(a.data[0].size, a.data.size, 0f)
            for (i in a.data.indices)
                for (j in a.data[i].indices)
                    result.data[j][i] = a.data[i][j]
            return result
        }

    }
}