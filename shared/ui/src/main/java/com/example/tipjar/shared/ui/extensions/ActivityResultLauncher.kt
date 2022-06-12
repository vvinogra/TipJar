package com.example.tipjar.shared.ui.extensions

import androidx.activity.result.ActivityResultLauncher

/**
 * https://stackoverflow.com/questions/69072654/android-camera-intent-not-working-in-oneplus-devices
 */
fun <T> ActivityResultLauncher<T>.safeLaunch(input: T, onFailed: () -> Unit) {
    try {
        launch(input)
    } catch (e: Exception) {
        onFailed.invoke()
    }
}