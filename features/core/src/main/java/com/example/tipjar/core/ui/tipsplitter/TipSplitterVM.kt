package com.example.tipjar.core.ui.tipsplitter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TipSplitterVM @Inject constructor(
    private val tipSplitterModel: TipSplitterModel
): ViewModel() {

    private val _data = MutableStateFlow(provideDefaultTipSplitterData())

    val uiData = _data.map {

    }


    fun onTipHistoryClicked() {

    }

    private fun calculateTip(amount: Double, peopleCount: Int, tipPercentage: Int): TipCalculationResult {
        val totalTip = amount * tipPercentage / 100
        val perPerson = totalTip / peopleCount

        return TipCalculationResult(totalTip, perPerson)
    }

    private fun provideDefaultTipSplitterData(): TipSplitterData {
        return TipSplitterData(
            tipPercentage = 10.0,
            peopleCount = 1,
            totalAmount = null
        )
    }
}

data class TipCalculationResult(
    val total: Double,
    val perPerson: Double
)

data class TipSplitterData(
    val tipPercentage: Double,
    val peopleCount: Int,
    val totalAmount: Double?
)