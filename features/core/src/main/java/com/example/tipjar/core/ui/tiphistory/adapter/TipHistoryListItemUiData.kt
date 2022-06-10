package com.example.tipjar.core.ui.tiphistory.adapter

data class TipHistoryListItemUiData(
    val date: String,
    val totalAmount: String,
    val totalTipAmount: String,
    val imagePath: String?,
    val onTipHistoryItemImageClick: () -> Unit
)