package com.example.tipjar.changecurrency.ui.model

import com.example.tipjar.data.currency.model.CurrencyItem

data class SelectCurrencyData(
    val currencyList: List<CurrencyListItemUiData>,
    val searchQuery: String,
    val selectedCurrency: CurrencyItem,
    val selectedItemPosition: Int?
)

data class FilteredCurrencyListData(
    val currencyList: List<CurrencyListItemUiData>,
    val selectedItemPosition: Int?
)