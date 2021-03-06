package com.example.tipjar.core.ui.tipsplitter

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterNavigation
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterUserInputData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MIN_PERCENTAGE_VALUE = 0
const val MAX_PERCENTAGE_VALUE = 100

@HiltViewModel
class TipSplitterVM @Inject constructor(
    private val tipSplitterModel: TipSplitterModel
) : ViewModel() {

    private var savedOriginalImageUri: Uri? = null

    private val _data = MutableStateFlow(tipSplitterModel.provideDefaultTipSplitterData())
    val uiData = _data.asStateFlow()

    init {
        viewModelScope.launch {
            tipSplitterModel.currencySelectionUpdatedFlow
                .collectLatest {
                    _data.update { data ->
                       tipSplitterModel.getUpdatedDataAfterCurrencyChange(data, it)
                    }
                }
        }
    }

    fun onTotalAmountChanged(text: String) {
        if (_data.value.totalAmount.userInput == text) {
            // Ignoring
            return
        }

        updateTipSplitterAfterCalculation(
            totalAmount = TipSplitterUserInputData(
                text,
                text.toDoubleOrNull()
            )
        )
    }

    fun onTipPercentageChanged(text: String) {
        val newTipPercentage = text.toIntOrNull()

        if (_data.value.tipPercentage == newTipPercentage) {
            // Ignoring
            return
        }

        updateTipSplitterAfterCalculation(
            tipPercentage = newTipPercentage,
            setForceNullableTipPercentage = true
        )
    }

    fun onPlusButtonClicked() {
        val newPeopleCount = _data.value.peopleCount + 1

        updateTipSplitterAfterCalculation(peopleCount = newPeopleCount)
    }

    fun onMinusButtonClicked() {
        val newPeopleCount = _data.value.peopleCount - 1

        if (newPeopleCount < 1) {
            // Ignoring click
            return
        }

        updateTipSplitterAfterCalculation(peopleCount = newPeopleCount)
    }

    fun onTakePhotoOfReceiptCheckedChanged(isChecked: Boolean) {
        if (isChecked == _data.value.shouldTakePhotoOfReceipt) {
            // Ignoring click
            return
        }

        _data.update {
            it.copy(
                shouldTakePhotoOfReceipt = isChecked
            )
        }
    }

    private fun updateTipSplitterAfterCalculation(
        totalAmount: TipSplitterUserInputData<Double?>? = null,
        peopleCount: Int? = null,
        tipPercentage: Int? = null,
        setForceNullableTipPercentage: Boolean = false
    ) {
        _data.update {
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = it,
                totalAmount = totalAmount,
                peopleCount = peopleCount,
                tipPercentage = tipPercentage,
                setForceNullableTipPercentage = setForceNullableTipPercentage
            )
        }
    }

    fun onUserTookReceiptPhoto(didUserTookPhoto: Boolean) {
        viewModelScope.launch {
            if (!didUserTookPhoto) {
                // Ignoring
                return@launch
            }

            val data = _data.value

            val cachedSavedOriginalImageUri = savedOriginalImageUri ?: return@launch
            savedOriginalImageUri = null

            tipSplitterModel.saveTipInHistory(data, cachedSavedOriginalImageUri)

            _data.update {
                it.copy(navigationEvent = TipSplitterNavigation.TipHistory)
            }
        }
    }

    fun onFailedToOpenExternalCameraApp() {
        _data.update {
            it.copy(showCantOpenCameraToast = Unit)
        }
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            val data = _data.value

            savedOriginalImageUri = null

            if (data.shouldTakePhotoOfReceipt) {
                if (tipSplitterModel.canTakePhotoOfReceipt()) {
                    tipSplitterModel.createUriToSaveOriginalImage()?.let { uri ->
                        savedOriginalImageUri = uri

                        _data.update {
                            it.copy(navigationEvent = TipSplitterNavigation.TakePhotoOfReceipt(uri))
                        }
                    }
                } else {
                    _data.update {
                        it.copy(showCantOpenCameraToast = Unit)
                    }
                }

                return@launch
            }

            tipSplitterModel.saveTipInHistory(data)

            _data.update {
                it.copy(navigationEvent = TipSplitterNavigation.TipHistory)
            }
        }
    }

    fun onTipHistoryClicked() {
        _data.update {
            it.copy(navigationEvent = TipSplitterNavigation.TipHistory)
        }
    }

    fun onChangeCurrencyClicked() {
        _data.update {
            it.copy(navigationEvent = TipSplitterNavigation.ChangeCurrency)
        }
    }

    fun navigationEventHandled() {
        _data.update {
            it.copy(navigationEvent = null)
        }
    }

    fun cantOpenCameraToastMessageDisplayed() {
        _data.update {
            it.copy(showCantOpenCameraToast = null)
        }
    }
}