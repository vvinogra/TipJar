package com.example.tipjar.changecurrency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FILTERING_DELAY = 100L

@HiltViewModel
class SelectCurrencyVM @Inject constructor(
    private val selectCurrencyModel: SelectCurrencyModel
) : ViewModel() {
    private val _selectCurrencyUiData = MutableStateFlow(selectCurrencyModel.provideDefaultSelectCurrencyData())

    val filteredCurrencyList = selectCurrencyModel.getFilteredCurrencyTransformedFlow(
        _selectCurrencyUiData
    )

    init {
        viewModelScope.launch {
            val currencies = selectCurrencyModel.loadCurrencies()

            _selectCurrencyUiData.update {
                it.copy(
                    currencyList = currencies.asCurrencyListItemUiDataList(
                        selectCurrencyModel.getSelectedCurrencyItem()
                    )
                )
            }
        }
    }

    fun onSearchQueryUpdated(newQuery: String) {
        viewModelScope.launch {
            delay(FILTERING_DELAY)

            _selectCurrencyUiData.update {
                it.copy(searchQuery = newQuery)
            }
        }
    }

    private fun updateSelectedItem(newSelectedItem: CurrencyItem) {
        viewModelScope.launch {
            selectCurrencyModel.selectCurrencyItem(newSelectedItem)

            _selectCurrencyUiData.update { data ->
                data.copy(
                    selectedCurrency = newSelectedItem,
                    currencyList = data.currencyList.map {
                        it.copy(
                            isSelected = newSelectedItem.currencyCode == it.code
                        )
                    },
                )
            }
        }
    }

    private fun Collection<CurrencyItem>.asCurrencyListItemUiDataList(
        selectedItem: CurrencyItem
    ) : List<CurrencyListItemUiData> {
        return map {
            CurrencyListItemUiData(
                code = it.currencyCode,
                symbol = it.symbol,
                name = it.displayName,
                isSelected = it.currencyCode == selectedItem.currencyCode,
                clickListener = {
                    if (_selectCurrencyUiData.value.selectedCurrency.currencyCode == it.currencyCode) {
                        // Ignoring
                        return@CurrencyListItemUiData
                    }

                    updateSelectedItem(it)
                },
                clickData = it
            )
        }
    }
}