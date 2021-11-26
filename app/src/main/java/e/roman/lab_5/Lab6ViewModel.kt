package e.roman.lab_5

import android.util.Log
import androidx.lifecycle.ViewModel

class Lab6ViewModel: ViewModel() {

    fun solveExplicit() {
        Log.d("checkk", "btn solve explicit clicked")
    }

    fun solveImplicit() {
        Log.d("checkk", "btn solve implicit clicked")
    }

    fun tableLeft() {
        Log.d("checkk", "btn left clicked")
    }

    fun tableRight() {
        Log.d("checkk", "btn right clicked")
    }
}