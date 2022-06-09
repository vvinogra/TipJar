package com.example.tipjar.core.ui.tiphistory.model

import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryListItemUiData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterNavigation
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

sealed class TipHistoryNavigation {
    data class OpenFullSizedTipHistoryItem(val id: Int): TipHistoryNavigation()
}

data class TipHistoryUiData(
    val tipHistoryList: List<TipHistoryListItemUiData>,
    val navigation: TipHistoryNavigation?
)

data class TipHistoryData(
    val tipHistoryList: List<TipHistoryEntity>
)