package com.example.tipjar.shared.ui.extensions

import androidx.activity.result.ActivityResultLauncher

fun ActivityResultLauncher<Unit>.launchUnit() {
    launch(Unit)
}