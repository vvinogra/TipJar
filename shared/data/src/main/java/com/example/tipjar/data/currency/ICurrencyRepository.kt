package com.example.tipjar.data.currency

import com.example.tipjar.data.currency.model.CurrencyItem
import kotlinx.coroutines.flow.SharedFlow

interface ICurrencyRepository {
    val currencySelectionUpdatedFlow: SharedFlow<CurrencyItem>

    suspend fun selectCurrency(currencyItem: CurrencyItem)
    fun getSelectedCurrency(): CurrencyItem
    fun getCurrencyItemFromCode(code: String): CurrencyItem
    fun getAvailableCurrencies(): Set<CurrencyItem>
}