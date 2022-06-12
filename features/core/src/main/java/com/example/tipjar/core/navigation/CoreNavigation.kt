package com.example.tipjar.core.navigation

import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.core.ui.tiphistory.TipHistoryFragment
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragment

interface CoreNavigation {
    fun fromTipSplitterToTipHistory(tipSplitterFragment: TipSplitterFragment)

    fun fromTipSplitterToSelectCurrency(tipSplitterFragment: TipSplitterFragment)

    fun fromTipHistoryToTipDetails(
        tipHistoryFragment: TipHistoryFragment,
        tipDetailsNavValues: TipDetailsNavValues
    )
}