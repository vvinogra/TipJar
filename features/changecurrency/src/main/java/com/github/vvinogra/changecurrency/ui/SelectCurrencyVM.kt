package com.github.vvinogra.changecurrency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.data.currency.model.CurrencyItem
import com.github.vvinogra.changecurrency.ui.model.CurrencyListItemUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyVM @Inject constructor(
    private val selectCurrencyModel: SelectCurrencyModel
) : ViewModel() {
    private val _selectCurrencyUiData = MutableStateFlow(selectCurrencyModel.provideDefaultSelectCurrencyData())
    val selectCurrencyUiData = _selectCurrencyUiData

    init {
        viewModelScope.launch {
            val currencies = selectCurrencyModel.loadCurrencies()

            _selectCurrencyUiData.update {
                it.copy(currencyList = currencies.asCurrencyListItemUiDataList(null))
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
                        it.copy(isSelected = it.code == newSelectedItem.currencyCode)
                    }
                )
            }
        }
    }

    private fun Collection<CurrencyItem>.asCurrencyListItemUiDataList(
        selectedItem: CurrencyItem?
    ) : List<CurrencyListItemUiData> {
        return map {
            CurrencyListItemUiData(
                code = it.currencyCode,
                symbol = it.symbol,
                name = it.displayName,
                isSelected = it.currencyCode == selectedItem?.currencyCode,
                clickListener = {
                    if (selectedItem?.currencyCode == it.currencyCode) {
                        // Ignoring
                        return@CurrencyListItemUiData
                    }

                    updateSelectedItem(it)
                }
            )
        }
    }
}