package com.example.tipjar.changecurrency.ui

import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData
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
            selectedCurrency = getSelectedCurrencyItem()
        )
    }

    fun getFilteredCurrencyListDataTransformedFlow(
        originalFlow: Flow<SelectCurrencyData>
    ): Flow<FilteredCurrencyListData> =
        originalFlow.transform { data ->
            if (data.searchQuery.isEmpty()) {
                emit(data.currencyList.asFilteredCurrencyListData(data.selectedCurrency))
                return@transform
            }

            val query = data.searchQuery.asSearchFilterString()

            val filteredList = data.currencyList.filter {
                it.name.asSearchFilterString().contains(query) ||
                    it.code.asSearchFilterString().contains(query)
            }

            emit(filteredList.asFilteredCurrencyListData(data.selectedCurrency))
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

    private fun List<CurrencyListItemUiData>.asFilteredCurrencyListData(
        selectedCurrencyItem: CurrencyItem
    ): FilteredCurrencyListData {
        return FilteredCurrencyListData(
            currencyList = this,
            selectedItemPosition = indexOfFirst {
                it.code == selectedCurrencyItem.currencyCode
            }
        )
    }

    private fun String.asSearchFilterString(): String {
        return this.trim().lowercase()
    }
}