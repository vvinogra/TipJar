package com.example.tipjar.core.ui.tiphistory.model

import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

sealed class TipHistoryNavigation {
    data class OpenFullSizedTipHistoryItem(
        val tipDetailsNavValues: TipDetailsNavValues
    ): TipHistoryNavigation()
}

data class TipHistoryUiData(
    val showUndoDeleteSnackbarEvent: Unit?,
    val navigation: TipHistoryNavigation?,
    val historyList: List<TipHistoryListItemUiData>
)

data class TipHistoryData(
    val tipHistoryList: List<TipHistoryEntity>
)