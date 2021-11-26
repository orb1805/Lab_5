package e.roman.lab_5

import android.app.Application
import androidx.lifecycle.ViewModel

class App: Application(), ViewModelProvider {

    private val viewModels: MutableMap<String?, ViewModel> = mutableMapOf(
        Lab6Activity::class.qualifiedName to Lab6ViewModel()
    )

    override fun getViewModel(key: String) : ViewModel =
        viewModels[key]!!
}