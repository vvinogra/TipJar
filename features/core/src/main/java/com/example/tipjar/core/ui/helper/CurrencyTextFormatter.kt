package com.example.tipjar.core.ui.helper

import com.example.tipjar.data.currency.model.CurrencyItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class CurrencyTextFormatter @Inject constructor() {

    fun formatValueWithCurrencyCodeAndFractionalDigits(
        value: Double,
        currencyItem: CurrencyItem,
        useCurrencySymbol: Boolean = true
    ) : String {
        // Some currencies may have a negative [defaultFractionDigits] value
        val fractionalCurrencyDigits = max(currencyItem.defaultFractionDigits, 0)

        val template = if (useCurrencySymbol) {
            "${currencyItem.symbol}%.${fractionalCurrencyDigits}f"
        } else {
            "%.${fractionalCurrencyDigits}f"
        }

        return String.format(template, value)
    }
}