package com.example.tipjar.shared.ui.extensions

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.moveCursorToEnd() { setSelection(text.length) }

fun EditText.doOnActionDone(action: () -> Unit) =
    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                action.invoke()
            }
        }
        false
    }

fun EditText.text(): String = text.toString()