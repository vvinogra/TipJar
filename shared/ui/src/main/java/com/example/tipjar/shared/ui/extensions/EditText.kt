package com.example.tipjar.shared.ui.extensions

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.util.TypedValue
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged

const val ZERO_PROGRESS_VALUE = "0"

typealias OnEditTextClickIcon = (Unit) -> Unit

fun EditText.setEditTextBackground(@DrawableRes backgroundId: Int) {
    background = resources.getDrawable(backgroundId, null)
}

fun EditText.setHintOnlyWhenEmpty(@StringRes hintRes: Int) {
    addTextChangedListener {
        if (text.toString().isEmpty()) {
            setHint(hintRes)
        } else {
            hint = ""
        }
    }
    if (text.isEmpty()) {
        setHint(hintRes)
    }
}

fun EditText.moveCursorToEnd() { setSelection(text.length) }

fun EditText.hideCursor() { isCursorVisible = false }

fun EditText.showCursor() { isCursorVisible = true }
//
//@Deprecated("Deprecated in new design")
//fun EditText.setListenerWithMaxRgbLimit(listener: (Int) -> Unit) {
//    doAfterTextChanged {
//        val text = it.toString()
//        if (text.length > 2) {
//            val number = it.toString().toInt()
//            if (number > resources.getInteger(R.integer.sw_rgb_max_value)) {
//                setText(text.dropLast(1))
//                return@doAfterTextChanged
//            }
//        }
//        val zeroProgressValueLength = ZERO_PROGRESS_VALUE.length
//        if (text.length > zeroProgressValueLength && text.startsWith(ZERO_PROGRESS_VALUE)) {
//            removeFirstSymbols(zeroProgressValueLength)
//        }
//        if (isFocused) {
//            if (text.isNotEmpty()) {
//                listener(text.toInt())
//            } else {
//                listener(0)
//            }
//        }
//        moveCursorToEnd()
//    }
//}

inline fun Editable.setMaxLimitListener(value: String?, maxValue: Int, onMaxLimitReached: (Int) -> Unit) {
    val text = value ?: toString()
    val number = text.toIntOrNull() ?: return

    if (number > maxValue) {
        onMaxLimitReached(number)
    }
}

fun Editable.applyZeroNumberFilter(value: String?) {
    val text = value ?: toString()
    val zeroProgressValueLength = ZERO_PROGRESS_VALUE.length
    if (text.length > zeroProgressValueLength && text.startsWith(ZERO_PROGRESS_VALUE)) {
        delete(0, zeroProgressValueLength)
    }
}


fun EditText.setSelectionPost(position: Int, predicate: ((EditText) -> Boolean)? = null) {
    post {
        if (predicate == null || predicate.invoke(this)) {
            setSelection(position)
        }
    }
}

fun EditText.removeFirstSymbols(symbolsCount: Int) {
    text?.delete(0, symbolsCount)
}

fun EditText.removeLastSymbols(symbolsCount: Int) {
    setText(text.substring(0, text.length - symbolsCount))
    setSelection(text.length)
}

fun EditText.text(): String = text.toString()

//fun EditText.numberTextOrZero(): String = text().ifEmpty { ZERO_PROGRESS_VALUE }
//
//fun EditText.setZeroIfEmpty() {
//    setTextIfEmpty(ZERO_PROGRESS_VALUE)
//}

//fun EditText.setTextIfEmpty(text: String) {
//    if (text().isEmpty()) {
//        setText(text)
//        moveCursorToEnd()
//    }
//}
//
//fun EditText.setStartDrawable(@DrawableRes drawableId: Int?) =
//    setCompoundDrawablesWithIntrinsicBounds(
//        drawableId?.let { ContextCompat.getDrawable(context, drawableId) }, null, null, null
//    )
//
//fun EditText.setEndDrawable(@DrawableRes drawableId: Int?) =
//    setCompoundDrawablesWithIntrinsicBounds(
//        null, null, drawableId?.let { ContextCompat.getDrawable(context, drawableId) }, null
//    )
//
//fun EditText.setLengthFilter(maxLength: Int, onOverflow: (Int) -> Unit) {
//    filters = filters.plus(object: InputFilter.LengthFilter(maxLength) {
//        override fun filter(
//            source: CharSequence?,
//            start: Int,
//            end: Int,
//            dest: Spanned?,
//            dstart: Int,
//            dend: Int
//        ): CharSequence? {
//            val res = super.filter(source, start, end, dest, dstart, dend)
//
//            if (res != null) {
//                onOverflow(max)
//            }
//
//            return res
//        }
//    })
//}
//
//@SuppressLint("ClickableViewAccessibility")
//fun EditText.doOnIconTouch(action: OnEditTextClickIcon) {
//    this.setOnTouchListener { _, event ->
//        val offset = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            resources.getDimension(R.dimen.margin_large_extra),
//            resources.displayMetrics
//        )
//        if (event.action == MotionEvent.ACTION_UP) {
//            if (event.rawX >= (this.right - offset)) {
//                action.invoke(Unit)
//                return@setOnTouchListener true
//            }
//        }
//        return@setOnTouchListener false
//    }
//}
//
//fun EditText.doAfterTextChangedWhenFocused(
//    action: (text: Editable?) -> Unit
//) {
//    doAfterTextChanged {
//        if (hasFocus()) {
//            action(it)
//        }
//    }
//}
//
//fun EditText.doOnActionDone(action: (() -> Unit)? = null) =
//    setOnEditorActionListener { _, actionId, _ ->
//        when (actionId) {
//            EditorInfo.IME_ACTION_DONE -> {
//                hideKeyboard()
//                clearFocus()
//                action?.invoke()
//            }
//        }
//        false
//    }