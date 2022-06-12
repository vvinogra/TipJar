package com.example.tipjar.core.ui.tipsplitter.model

data class TipSplitterData(
    val tipPercentage: Int?,
    val tipPercentageHintValue: Int,
    val peopleCount: Int,
    val totalAmount: Double?,
    val totalAmountHintValue: TipSplitterFormattedDoubleValue,
    val totalTip: TipSplitterFormattedDoubleValue,
    val perPersonTip: TipSplitterFormattedDoubleValue,
    val shouldTakePhotoOfReceipt: Boolean,
    val currencyCode: String,
    val fractionalCurrencyDigits: Int,
    val currencySymbol: String,
    val showCantOpenCameraToast: Unit?,
    val navigationEvent: TipSplitterNavigation?
)

data class TipSplitterFormattedDoubleValue(
    val originalValue: Double,
    val formattedValue: String
)