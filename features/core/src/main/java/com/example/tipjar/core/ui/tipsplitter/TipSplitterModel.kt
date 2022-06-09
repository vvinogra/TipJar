package com.example.tipjar.core.ui.tipsplitter

import android.graphics.Bitmap
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.data.coroutines.DispatcherProvider
import com.example.tipjar.data.phonefeature.IUserPhoneFeatureManager
import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.math.max


class TipSplitterModel @Inject constructor(
    private val tipHistoryRepository: ITipHistoryRepository,
    private val userPhoneFeatureManager: IUserPhoneFeatureManager,
    private val dispatcherProvider: DispatcherProvider
) {
    fun provideDefaultTipSplitterData(): TipSplitterData {
        val tipPercentage = 10
        val peopleCount = 1
        val totalAmount = 100.0

        val tipCalculationResult = calculateTip(totalAmount, peopleCount, tipPercentage)

        val currency = Currency.getInstance("USD")

        return TipSplitterData(
            tipPercentage = tipPercentage,
            tipPercentageHintValue = tipPercentage,
            peopleCount = peopleCount,
            totalAmount = null,
            totalAmountHintValue = totalAmount,
            totalTip = tipCalculationResult.total,
            perPersonTip = tipCalculationResult.perPerson,
            shouldTakePhotoOfReceipt = false,
            // Some currencies may have a negative [defaultFractionDigits] value
            fractionalCurrencyDigits = max(currency.defaultFractionDigits, 0),
            currencySymbol = currency.symbol,
            toastMessage = null,
            navigationEvent = null
        )
    }

    fun canTakePhotoOfReceipt(): Boolean {
        return userPhoneFeatureManager.canOpenExternalImageCapture()
    }

    suspend fun saveTipInHistory(data: TipSplitterData, bitmap: Bitmap? = null) =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.createTipHistoryRecord(
                totalAmount = data.totalAmount ?: data.totalAmountHintValue,
                tipAmount = data.totalTip,
                receiptBitmap = bitmap
            )
        }

    fun getUpdatedTipSplitterAfterCalculation(
        data: TipSplitterData,
        totalAmount: Double? = null,
        peopleCount: Int? = null,
        tipPercentage: Int? = null,
        setForceNullableTotalAmount: Boolean = false,
        setForceNullableTipPercentage: Boolean = false
    ) : TipSplitterData {
        val totalAmountToUpdate = if (setForceNullableTotalAmount) {
            totalAmount
        } else {
            totalAmount ?: data.totalAmount
        }
        val peopleCountToUpdate = peopleCount ?: data.peopleCount
        val tipPercentageToUpdate = if (setForceNullableTipPercentage) {
            tipPercentage
        } else {
            tipPercentage ?: data.tipPercentage
        }

        val tipCalculationResult = calculateTip(
            totalAmount = totalAmountToUpdate ?: data.totalAmountHintValue,
            peopleCount = peopleCountToUpdate,
            tipPercentage = tipPercentageToUpdate ?: data.tipPercentageHintValue
        )

        return data.copy(
            tipPercentage = tipPercentageToUpdate,
            peopleCount = peopleCountToUpdate,
            totalAmount = totalAmountToUpdate,
            totalTip = tipCalculationResult.total,
            perPersonTip = tipCalculationResult.perPerson
        )
    }

    private fun calculateTip(
        totalAmount: Double,
        peopleCount: Int,
        tipPercentage: Int
    ): TipCalculationResult {
        val totalTip = totalAmount * tipPercentage / 100
        val perPerson = totalTip / peopleCount

        return TipCalculationResult(totalTip, perPerson)
    }

    private data class TipCalculationResult(
        val total: Double,
        val perPerson: Double
    )
}