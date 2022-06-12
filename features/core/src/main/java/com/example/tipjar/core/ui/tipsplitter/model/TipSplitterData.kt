package com.example.tipjar.core.ui.tipsplitter.model

import com.example.tipjar.data.currency.model.CurrencyItem

data class TipSplitterData(
    val tipPercentage: Int?,
    val tipPercentageHintValue: Int,
    val peopleCount: Int,
    val totalAmount: Double?,
    val totalAmountHintValue: TipSplitterFormattedDoubleValue,
    val totalTip: TipSplitterFormattedDoubleValue,
    val perPersonTip: TipSplitterFormattedDoubleValue,
    val shouldTakePhotoOfReceipt: Boolean,
    val selectedCurrency: CurrencyItem,
    val showCantOpenCameraToast: Unit?,
    val navigationEvent: TipSplitterNavigation?
)

data class TipSplitterFormattedDoubleValue(
    val originalValue: Double,
    val formattedValue: String
)