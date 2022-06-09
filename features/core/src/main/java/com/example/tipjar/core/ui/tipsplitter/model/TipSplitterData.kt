package com.example.tipjar.core.ui.tipsplitter.model

import androidx.annotation.StringRes

data class TipSplitterData(
    val tipPercentage: Int?,
    val tipPercentageHintValue: Int,
    val peopleCount: Int,
    val totalAmount: Double?,
    val totalAmountHintValue: Double,
    val totalTip: Double,
    val perPersonTip: Double,
    val shouldTakePhotoOfReceipt: Boolean,
    val currencyCode: String,
    val fractionalCurrencyDigits: Int,
    val currencySymbol: String,
    @StringRes val toastMessage: Int?,
    val navigationEvent: TipSplitterNavigation?
)