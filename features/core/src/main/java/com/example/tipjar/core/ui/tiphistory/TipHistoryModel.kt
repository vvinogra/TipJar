package com.example.tipjar.core.ui.tiphistory

import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
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
    suspend fun getTipHistoryList(): List<TipHistoryEntity> =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.getAllTipHistoryRecords()
        }

    fun getTipHistoryImagePathById(id: Int): String? =
        tipHistoryRepository.getTipHistoryImagePathById(id)

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