package com.github.vvinogra.changecurrency.ui.model

import com.example.tipjar.data.currency.model.CurrencyItem

data class SelectCurrencyData(
    val currencyList: List<CurrencyListItemUiData>,
    val selectedCurrency: CurrencyItem?
)