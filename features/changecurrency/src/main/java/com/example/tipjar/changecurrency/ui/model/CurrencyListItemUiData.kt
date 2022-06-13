package com.example.tipjar.changecurrency.ui.model

import com.example.tipjar.data.currency.model.CurrencyItem

data class CurrencyListItemUiData(
    val code: String,
    val symbol: String,
    val name: String,
    val isSelected: Boolean,
    // Please, use this item only for click events
    val clickData: CurrencyItem,
    val clickListener: () -> Unit
)