package com.example.tipjar.core.ui.tipsplitter

import android.net.Uri
import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterFormattedDoubleValue
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
    private val currencyTextFormatter: CurrencyTextFormatter,
    private val dispatcherProvider: DispatcherProvider
) {
    fun provideDefaultTipSplitterData(): TipSplitterData {
        val tipPercentage = 10
        val peopleCount = 1
        val totalAmount = 100.0

        val tipCalculationResult = calculateTip(totalAmount, peopleCount, tipPercentage)

        val defaultCurrencyCode = "USD"
        val currency = Currency.getInstance(defaultCurrencyCode)

        return TipSplitterData(
            tipPercentage = tipPercentage,
            tipPercentageHintValue = tipPercentage,
            peopleCount = peopleCount,
            totalAmount = null,
            totalAmountHintValue = totalAmount.asTipSplitterFormattedDoubleValue(
                currencyCode = defaultCurrencyCode,
                useCurrencySymbol = false
            ),
            totalTip = tipCalculationResult.total.asTipSplitterFormattedDoubleValue(defaultCurrencyCode),
            perPersonTip = tipCalculationResult.perPerson.asTipSplitterFormattedDoubleValue(defaultCurrencyCode),
            shouldTakePhotoOfReceipt = false,
            currencyCode = defaultCurrencyCode,
            // Some currencies may have a negative [defaultFractionDigits] value
            fractionalCurrencyDigits = max(currency.defaultFractionDigits, 0),
            currencySymbol = currency.symbol,
            showCantOpenCameraToast = null,
            navigationEvent = null
        )
    }

    fun canTakePhotoOfReceipt(): Boolean {
        return userPhoneFeatureManager.isCameraAvailable()
    }

    fun createUriToSaveOriginalImage(): Uri? {
        return tipHistoryRepository.createUriToSaveReceiptImage()
    }

    private fun getFormattedAmountWithCurrency(
        value: Double,
        currencyCode: String,
        useCurrencySymbol: Boolean
    ): String {
        val currency = Currency.getInstance(currencyCode)

        return currencyTextFormatter.formatValueWithCurrencyCodeAndFractionalDigits(
            value,
            currency,
            useCurrencySymbol
        )
    }

    suspend fun saveTipInHistory(data: TipSplitterData, uri: Uri? = null) =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.createTipHistoryRecord(
                totalAmount = data.totalAmount ?: data.totalAmountHintValue.originalValue,
                tipAmount = data.totalTip.originalValue,
                receiptImageUri = uri,
                currencyCode = data.currencyCode
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
            totalAmount = totalAmountToUpdate ?: data.totalAmountHintValue.originalValue,
            peopleCount = peopleCountToUpdate,
            tipPercentage = tipPercentageToUpdate ?: data.tipPercentageHintValue
        )

        return data.copy(
            tipPercentage = tipPercentageToUpdate,
            peopleCount = peopleCountToUpdate,
            totalAmount = totalAmountToUpdate,
            totalTip = tipCalculationResult.total.asTipSplitterFormattedDoubleValue(data.currencyCode),
            perPersonTip = tipCalculationResult.perPerson.asTipSplitterFormattedDoubleValue(data.currencyCode)
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

    private fun Double.asTipSplitterFormattedDoubleValue(
        currencyCode: String,
        useCurrencySymbol: Boolean = true
    ): TipSplitterFormattedDoubleValue {
        return TipSplitterFormattedDoubleValue(
            originalValue = this,
            formattedValue = getFormattedAmountWithCurrency(this, currencyCode, useCurrencySymbol)
        )
    }

    private data class TipCalculationResult(
        val total: Double,
        val perPerson: Double
    )
}