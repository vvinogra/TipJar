package com.github.vvinogra.changecurrency.ui.model

data class CurrencyListItemUiData(
    val code: String,
    val symbol: String,
    val name: String,
    val isSelected: Boolean,
    val clickListener: () -> Unit
)