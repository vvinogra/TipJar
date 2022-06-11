package com.example.tipjar.core.ui.tiphistory

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryListItemUiData
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryNavigation
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipHistoryVM @Inject constructor(
    private val tipHistoryModel: TipHistoryModel
) : ViewModel() {

    private val _uiData = MutableStateFlow(tipHistoryModel.provideDefaultTipHistoryData())
    val uiData = _uiData.asStateFlow()

    private var recentlyDeletedItem: RecentlyDeletedItemData? = null

    init {
        viewModelScope.launch {
            val tipHistory = tipHistoryModel.getTipHistoryList()

            _uiData.update {
                it.copy(
                    historyList = tipHistory.asTipHistoryListItemUiDataList()
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiData.update {
            it.copy(
                navigation = null
            )
        }
    }

    fun onUndoSnackbarIsShown() {
        _uiData.update {
            it.copy(
                showUndoDeleteSnackbarEvent = null
            )
        }
    }

    fun undoRecentItemDeletion() {
        viewModelScope.launch {
            val recentlyDeletedCachedItem = recentlyDeletedItem
            recentlyDeletedItem = null

            if (recentlyDeletedCachedItem == null) {
                // Ignoring click
                return@launch
            }

            // We need to restore an item before updating the list because
            // we also save a bitmap in the filesystem if it exists
            tipHistoryModel.restoreTipHistoryEntity(
                recentlyDeletedCachedItem.item.clickItem,
                recentlyDeletedCachedItem.bitmap
            )

            _uiData.update { data ->
                val updatedHistoryList = data.historyList.toMutableList()

                updatedHistoryList.add(
                    recentlyDeletedCachedItem.position,
                    recentlyDeletedCachedItem.item
                )

                data.copy(
                    historyList = updatedHistoryList
                )
            }
        }
    }

    fun removeTipHistoryItem(
        tipHistoryListItemUiData: TipHistoryListItemUiData,
        position: Int,
        bitmap: Bitmap?
    ) {
        viewModelScope.launch {
            recentlyDeletedItem = RecentlyDeletedItemData(
                tipHistoryListItemUiData,
                position,
                bitmap
            )
            _uiData.update { data ->
                data.copy(
                    historyList = data.historyList.filterNot {
                        it.id == tipHistoryListItemUiData.id
                    },
                    showUndoDeleteSnackbarEvent = Unit
                )
            }

            tipHistoryModel.removeTipHistoryEntityById(tipHistoryListItemUiData.id)
        }
    }

    private fun List<TipHistoryEntity>.asTipHistoryListItemUiDataList(): List<TipHistoryListItemUiData> {
        return map {
            val totalAmount = it.getFormattedTotalAmount()
            val totalTipAmount = it.getFormattedTipTotalAmount()
            val date = tipHistoryModel.getFormattedDateString(it.timestamp)
            val imagePath = tipHistoryModel.getTipHistoryImagePathById(it.id)

            TipHistoryListItemUiData(
                id = it.id,
                date = date,
                totalAmount = totalAmount,
                totalTipAmount = totalTipAmount,
                imagePath = imagePath,
                clickItem = it,
                onTipHistoryItemImageClick = {
                    if (imagePath == null) return@TipHistoryListItemUiData

                    _uiData.update { uiData ->
                        uiData.copy(
                            navigation = TipHistoryNavigation.OpenFullSizedTipHistoryItem(
                                TipDetailsNavValues(
                                    date = date,
                                    totalAmount = totalAmount,
                                    tipTotalAmount = totalTipAmount,
                                    imagePath = imagePath
                                )
                            )
                        )
                    }
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

data class RecentlyDeletedItemData(
    val item: TipHistoryListItemUiData,
    val position: Int,
    val bitmap: Bitmap?
)