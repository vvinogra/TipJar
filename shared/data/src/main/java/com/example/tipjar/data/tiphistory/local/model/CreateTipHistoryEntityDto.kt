package com.example.tipjar.data.tiphistory.local.model

internal data class CreateTipHistoryEntityDto(
    val totalAmount: Double,
    val tipAmount: Double,
    val timestamp: Long
)