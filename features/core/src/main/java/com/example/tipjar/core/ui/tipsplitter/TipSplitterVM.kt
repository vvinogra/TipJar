package com.example.tipjar.core.ui.tipsplitter

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipSplitterVM @Inject constructor(
    private val tipSplitterModel: TipSplitterModel
) : ViewModel() {

    private val _data = MutableStateFlow(tipSplitterModel.provideDefaultTipSplitterData())
    val uiData = _data.asStateFlow()

    fun onTotalAmountChanged(text: String) {
        val newTotalAmount = text.toDoubleOrNull()

        updateTipSplitterAfterCalculation(
            totalAmount = newTotalAmount,
            setForceNullableTotalAmount = true
        )
    }

    fun onTipPercentageChanged(text: String) {
        val newTipPercentage = text.toIntOrNull()

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
        totalAmount: Double? = null,
        peopleCount: Int? = null,
        tipPercentage: Int? = null,
        setForceNullableTotalAmount: Boolean = false,
        setForceNullableTipPercentage: Boolean = false
    ) {
        _data.update {
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = it,
                totalAmount = totalAmount,
                peopleCount = peopleCount,
                tipPercentage = tipPercentage,
                setForceNullableTotalAmount = setForceNullableTotalAmount,
                setForceNullableTipPercentage = setForceNullableTipPercentage
            )
        }
    }

    fun onUserTookReceiptPhoto(bitmap: Bitmap?) {
        viewModelScope.launch {
            if (bitmap == null) {
                // Ignoring
                return@launch
            }

            val data = _data.value

            tipSplitterModel.saveTipInHistory(data, bitmap)

            _data.update {
                it.copy(navigationEvent = TipSplitterNavigation.TipHistory)
            }
        }
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            val data = _data.value

            if (data.shouldTakePhotoOfReceipt) {
                if (tipSplitterModel.canTakePhotoOfReceipt()) {
                    _data.update {
                        it.copy(navigationEvent = TipSplitterNavigation.TakePhotoOfReceipt)
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