package com.example.tipjar.data.tiphistory.model

data class TipHistoryEntity(
    val id: Int,
    val totalAmount: Double,
    val tipAmount: Double,
    val timestamp: Long
)