package com.example.tipjar.core.ui.tipsplitter.model

data class TipSplitterData(
    val tipPercentage: Int?,
    val tipPercentageHintValue: Int,
    val peopleCount: Int,
    val totalAmount: Double?,
    val totalAmountHintValue: Double,
    val totalTip: Double,
    val perPersonTip: Double,
    val shouldTakePhotoOfReceipt: Boolean,
    val navigationEvent: TipSplitterNavigation?
)