package com.example.tipjar.shared.ui.util.edittext.inputfilter

import android.text.InputFilter
import android.text.Spanned

class MinMaxInputFilter(minValue: Int = 0, maxValue: Int = 0) : InputFilter {

    private val rangeMinMax = IntRange(minValue, maxValue)

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        try {
            val inputString = dest.toString()
            val intSubstring = Integer.parseInt(
                inputString.substring(0, dstart) + source.subSequence(start, end) + inputString.substring(dend)
            )

            if (rangeMinMax.contains(intSubstring)) {
                return null
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

}