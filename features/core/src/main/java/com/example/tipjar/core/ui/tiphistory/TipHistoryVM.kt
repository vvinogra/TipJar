package com.example.tipjar.core.ui.tiphistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryListItemUiData
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryNavigation
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipHistoryVM @Inject constructor(
    private val tipHistoryModel: TipHistoryModel
) : ViewModel() {
    private val _tipHistoryUiList = MutableStateFlow<List<TipHistoryListItemUiData>>((emptyList()))
    val tipHistoryUiList = _tipHistoryUiList.asStateFlow()

    private val _navigation = MutableStateFlow<TipHistoryNavigation?>(null)
    val navigation = _navigation.asStateFlow()

    init {
        viewModelScope.launch {
            val tipHistory = tipHistoryModel.getTipHistoryList()

            _tipHistoryUiList.value = tipHistory.asTipHistoryListItemUiDataList()
        }
    }

    fun onNavigationHandled() {
        _navigation.value = null
    }

    private fun List<TipHistoryEntity>.asTipHistoryListItemUiDataList(): List<TipHistoryListItemUiData> {
        return map {
            TipHistoryListItemUiData(
                date = tipHistoryModel.getFormattedDateString(it.timestamp),
                totalAmount = it.getFormattedTotalAmount(),
                totalTipAmount = it.getFormattedTipTotalAmount(),
                imagePath = tipHistoryModel.getTipHistoryImagePathById(it.id),
                onTipHistoryClick = {
                    _navigation.value = TipHistoryNavigation.OpenFullSizedTipHistoryItem(it.id)
                }
            )
        }
    }

    private fun TipHistoryEntity.getFormattedTotalAmount(): String {
        return tipHistoryModel.getFormattedAmountWithCurrency(
            totalAmount,
            currencyCode
        )
    }

    private fun TipHistoryEntity.getFormattedTipTotalAmount(): String {
        return tipHistoryModel.getFormattedAmountWithCurrency(
            tipAmount,
            currencyCode
        )
    }
}