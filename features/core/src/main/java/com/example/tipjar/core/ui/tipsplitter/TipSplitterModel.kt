package com.example.tipjar.core.ui.tipsplitter

import android.net.Uri
import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterFormattedDoubleValue
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterUserInputData
import com.example.tipjar.data.coroutines.DispatcherProvider
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.data.phonefeature.IUserPhoneFeatureManager
import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

private const val DEFAULT_TIP_PERCENTAGE = 10
private const val DEFAULT_PEOPLE_COUNT = 1
private const val DEFAULT_TOTAL_AMOUNT = 100.0

class TipSplitterModel @Inject constructor(
    private val tipHistoryRepository: ITipHistoryRepository,
    private val currencyRepository: ICurrencyRepository,
    private val userPhoneFeatureManager: IUserPhoneFeatureManager,
    private val currencyTextFormatter: CurrencyTextFormatter,
    private val dispatcherProvider: DispatcherProvider
) {
    val currencySelectionUpdatedFlow = currencyRepository.currencySelectionUpdatedFlow

    fun provideDefaultTipSplitterData(): TipSplitterData {
        val tipPercentage = DEFAULT_TIP_PERCENTAGE
        val peopleCount = DEFAULT_PEOPLE_COUNT
        val totalAmount = DEFAULT_TOTAL_AMOUNT

        val tipCalculationResult = calculateTip(totalAmount, peopleCount, tipPercentage)

        val selectedCurrency = currencyRepository.getSelectedCurrency()

        return TipSplitterData(
            tipPercentage = tipPercentage,
            tipPercentageHintValue = tipPercentage,
            peopleCount = peopleCount,
            totalAmount = TipSplitterUserInputData("", null),
            totalAmountHintValue = totalAmount.asTipSplitterFormattedDoubleValue(
                currencyItem = selectedCurrency,
                useCurrencySymbol = false
            ),
            totalTip = tipCalculationResult.total.asTipSplitterFormattedDoubleValue(selectedCurrency),
            perPersonTip = tipCalculationResult.perPerson.asTipSplitterFormattedDoubleValue(selectedCurrency),
            shouldTakePhotoOfReceipt = false,
            selectedCurrency = selectedCurrency,
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

    suspend fun saveTipInHistory(data: TipSplitterData, uri: Uri? = null) =
        withContext(dispatcherProvider.io) {
            tipHistoryRepository.createTipHistoryRecord(
                totalAmount = data.totalAmount.value ?: data.totalAmountHintValue.originalValue,
                tipAmount = data.totalTip.originalValue,
                receiptImageUri = uri,
                currencyCode = data.selectedCurrency.currencyCode
            )
        }

    fun getUpdatedDataAfterCurrencyChange(
        data: TipSplitterData,
        newCurrencyItem: CurrencyItem
    ) : TipSplitterData {
        return with(data) {
            val updatedTotalAmount =
                if (newCurrencyItem.defaultFractionDigits != data.selectedCurrency.defaultFractionDigits) {
                    totalAmount.value?.toBigDecimal()?.setScale(
                        newCurrencyItem.defaultFractionDigits,
                        BigDecimal.ROUND_DOWN
                    )?.toDouble()
                } else {
                    data.totalAmount.value
                }

            val updatedTotalAmountUserInput =
                updatedTotalAmount?.asTipSplitterFormattedDoubleValue(
                    currencyItem = newCurrencyItem,
                    useCurrencySymbol = false
                )?.formattedValue.orEmpty()

            copy(
                totalAmount = TipSplitterUserInputData(
                    updatedTotalAmountUserInput,
                    updatedTotalAmount
                ),
                totalAmountHintValue = totalAmountHintValue.originalValue.asTipSplitterFormattedDoubleValue(
                    currencyItem = newCurrencyItem,
                    useCurrencySymbol = false
                ),
                totalTip = data.totalTip.originalValue.asTipSplitterFormattedDoubleValue(newCurrencyItem),
                perPersonTip = data.perPersonTip.originalValue.asTipSplitterFormattedDoubleValue(newCurrencyItem),
                selectedCurrency = newCurrencyItem
            )
        }
    }

    fun getUpdatedTipSplitterAfterCalculation(
        data: TipSplitterData,
        totalAmount: TipSplitterUserInputData<Double?>? = null,
        peopleCount: Int? = null,
        tipPercentage: Int? = null,
        setForceNullableTipPercentage: Boolean = false
    ) : TipSplitterData {
        val totalAmountToUpdate = totalAmount ?: data.totalAmount
        val peopleCountToUpdate = peopleCount ?: data.peopleCount
        val tipPercentageToUpdate = if (setForceNullableTipPercentage) {
            tipPercentage
        } else {
            tipPercentage ?: data.tipPercentage
        }

        val tipCalculationResult = calculateTip(
            totalAmount = totalAmountToUpdate.value ?: data.totalAmountHintValue.originalValue,
            peopleCount = peopleCountToUpdate,
            tipPercentage = tipPercentageToUpdate ?: data.tipPercentageHintValue
        )

        return data.copy(
            tipPercentage = tipPercentageToUpdate,
            peopleCount = peopleCountToUpdate,
            totalAmount = totalAmountToUpdate,
            totalTip = tipCalculationResult.total.asTipSplitterFormattedDoubleValue(data.selectedCurrency),
            perPersonTip = tipCalculationResult.perPerson.asTipSplitterFormattedDoubleValue(data.selectedCurrency)
        )
    }

    private fun getFormattedAmountWithCurrency(
        value: Double,
        currencyItem: CurrencyItem,
        useCurrencySymbol: Boolean
    ): String {
        return currencyTextFormatter.formatValueWithCurrencyCodeAndFractionalDigits(
            value,
            currencyItem,
            useCurrencySymbol
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
        currencyItem: CurrencyItem,
        useCurrencySymbol: Boolean = true
    ): TipSplitterFormattedDoubleValue {
        return TipSplitterFormattedDoubleValue(
            originalValue = this,
            formattedValue = getFormattedAmountWithCurrency(this, currencyItem, useCurrencySymbol)
        )
    }

    private data class TipCalculationResult(
        val total: Double,
        val perPerson: Double
    )
}