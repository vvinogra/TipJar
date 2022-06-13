package com.example.tipjar.changecurrency.ui

import com.example.tipjar.changecurrency.ui.model.FilteredCurrencyListData
import com.example.tipjar.data.coroutines.DispatcherProvider
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.changecurrency.ui.model.SelectCurrencyData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectCurrencyModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val currencyRepository: ICurrencyRepository
) {

    fun provideDefaultSelectCurrencyData(): SelectCurrencyData {
        return SelectCurrencyData(
            currencyList = emptyList(),
            searchQuery = "",
            selectedCurrency = getSelectedCurrencyItem(),
            selectedItemPosition = null
        )
    }

    fun getFilteredCurrencyListDataTransformedFlow(
        originalFlow: StateFlow<SelectCurrencyData>
    ): Flow<FilteredCurrencyListData> =
        originalFlow.transform { data ->
            if (data.searchQuery.isEmpty()) {
                emit(FilteredCurrencyListData(data.currencyList, data.selectedItemPosition))
                return@transform
            }

            val query = data.searchQuery.trim().lowercase()

            val filteredList = data.currencyList.filter {
                it.name.trim().lowercase().contains(query)
            }

            emit(FilteredCurrencyListData(filteredList, data.selectedItemPosition))
        }.distinctUntilChanged()
            .flowOn(dispatcherProvider.io)

    fun getSelectedCurrencyItem() =
        currencyRepository.getSelectedCurrency()

    suspend fun selectCurrencyItem(currencyItem: CurrencyItem) {
        withContext(dispatcherProvider.io) {
            currencyRepository.selectCurrency(currencyItem)
        }
    }

    suspend fun loadCurrencies(): Set<CurrencyItem> {
        return withContext(dispatcherProvider.io) {
            currencyRepository.getAvailableCurrencies()
                .toSortedSet { a, b ->
                    when {
                        (a.displayName < b.displayName) -> -1
                        (a.displayName > b.displayName) -> 1
                        else -> 0
                    }
                }
        }
    }
}