package com.example.tipjar.core.ui.tiphistory.model

import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

data class TipHistoryListItemUiData(
    val id: Int,
    val date: String,
    val totalAmount: String,
    val totalTipAmount: String,
    val imagePath: String?,
    // Please, use this item only for click events
    val clickItem: TipHistoryEntity,
    val onTipHistoryItemImageClick: () -> Unit
)