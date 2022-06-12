package com.example.tipjar.core.ui.tiphistory

import android.graphics.Bitmap
import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryUiData
import com.example.tipjar.data.coroutines.DispatcherProvider
import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TipHistoryModel @Inject constructor(
    private val tipHistoryRepository: ITipHistoryRepository,
    private val currencyTextFormatter: CurrencyTextFormatter,
    private val dispatcherProvider: DispatcherProvider
) {
    fun provideDefaultTipHistoryData(): TipHistoryUiData {
        return TipHistoryUiData(
            showUndoDeleteSnackbarEvent = null,
            navigation = null,
            historyList = emptyList(),
        )
    }

    suspend fun getTipHistoryList(): List<TipHistoryEntity> =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.getAllTipHistoryRecords()
        }

    suspend fun removeTipHistoryEntity(tipHistoryEntity: TipHistoryEntity) =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.removeTipHistoryRecord(tipHistoryEntity)
        }

    suspend fun restoreTipHistoryEntity(entity: TipHistoryEntity, image: Bitmap?) =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.restoreTipHistoryEntity(entity, image)
        }

    suspend fun getReceiptImageBitmap(entity: TipHistoryEntity): Bitmap? =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.getFullSizedReceiptImage(entity)
        }

    fun getReceiptImagePath(entity: TipHistoryEntity): String? =
        tipHistoryRepository.getReceiptImagePath(entity)

    fun getReceiptImageThumbPath(entity: TipHistoryEntity): String? =
        tipHistoryRepository.getReceiptThumbImagePath(entity)

    fun getFormattedDateString(
        timestamp: Long
    ): String {
        val dateFormat = SimpleDateFormat("yyyy MMMM dd", Locale.getDefault())
        val date = Date(timestamp)

        return dateFormat.format(date)
    }

    fun getFormattedAmountWithCurrency(
        value: Double,
        currencyCode: String
    ): String {
        val currency = Currency.getInstance(currencyCode)

        return currencyTextFormatter.formatValueWithCurrencyCodeAndFractionalDigits(
            value,
            currency
        )
    }
}