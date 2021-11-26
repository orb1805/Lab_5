package e.roman.lab_5

import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

interface ViewModelProvider {

    fun getViewModel(key: String): ViewModel
}