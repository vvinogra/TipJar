package com.github.vvinogra.changecurrency.ui

import com.example.tipjar.data.coroutines.DispatcherProvider
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import com.github.vvinogra.changecurrency.ui.model.SelectCurrencyData
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectCurrencyModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val currencyRepository: ICurrencyRepository
) {

    fun provideDefaultSelectCurrencyData(): SelectCurrencyData {
        return SelectCurrencyData(
            currencyList = emptyList(),
            selectedCurrency = null
        )
    }

    suspend fun selectCurrencyItem(currencyItem: CurrencyItem) {
        withContext(dispatcherProvider.io) {
            currencyRepository.selectCurrency(currencyItem)
        }
    }

    suspend fun loadCurrencies(): Set<CurrencyItem> {
        return withContext(dispatcherProvider.io) {
            currencyRepository.getAvailableCurrencies()
        }
    }
}