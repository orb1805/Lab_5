package e.roman.lab_5

import android.text.Editable
import java.lang.Exception

internal fun Editable?.toFloat() =
    try {
        toString().toFloat()
    } catch (e: Exception) {
        0f
    }

internal fun Editable?.toInt() =
    try {
        toString().toInt()
    } catch (e: Exception) {
        0
    }

