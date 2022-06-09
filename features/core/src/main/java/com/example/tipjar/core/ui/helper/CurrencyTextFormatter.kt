package com.example.tipjar.core.ui.helper

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class CurrencyTextFormatter @Inject constructor() {

    fun formatValueWithCurrencyCodeAndFractionalDigits(
        value: Double,
        currency: Currency,
        useCurrencySymbol: Boolean = true
    ) : String {
        val fractionalCurrencyDigits = max(currency.defaultFractionDigits, 0)

        val template = if (useCurrencySymbol) {
            "${currency.symbol}%.${fractionalCurrencyDigits}f"
        } else {
            "%.${fractionalCurrencyDigits}f"
        }

        return String.format(template, value)
    }
}