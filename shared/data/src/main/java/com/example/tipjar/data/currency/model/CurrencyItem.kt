package com.example.tipjar.data.currency.model

data class CurrencyItem(
    val currencyCode: String,
    val symbol: String,
    val displayName: String,
    val defaultFractionDigits: Int
)