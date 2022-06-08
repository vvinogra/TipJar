package com.example.tipjar.shared.ui.extensions

import android.view.Gravity
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.alignPrefixCenter() {
    prefixTextView.updateLayoutParams {
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }
    prefixTextView.gravity = Gravity.CENTER
}

fun TextInputLayout.alignSuffixCenter() {
    suffixTextView.updateLayoutParams {
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }
    suffixTextView.gravity = Gravity.CENTER
}