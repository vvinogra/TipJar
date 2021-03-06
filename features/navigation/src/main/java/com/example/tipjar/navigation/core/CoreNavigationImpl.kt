package com.example.tipjar.navigation.core

import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.core.ui.tiphistory.TipHistoryFragment
import com.example.tipjar.core.ui.tiphistory.TipHistoryFragmentDirections
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragment
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragmentDirections
import com.example.tipjar.navigation.navigate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CoreNavigationImpl @Inject constructor(): CoreNavigation {
    override fun fromTipSplitterToTipHistory(tipSplitterFragment: TipSplitterFragment) {
        val action = TipSplitterFragmentDirections.actionTipSplitterFragmentToTipHistoryFragment()

        tipSplitterFragment.navigate(action)
    }

    override fun fromTipSplitterToSelectCurrency(tipSplitterFragment: TipSplitterFragment) {
        val action = TipSplitterFragmentDirections.actionTipSplitterFragmentToSelectCurrencyFragment()

        tipSplitterFragment.navigate(action)
    }

    override fun fromTipHistoryToTipDetails(
        tipHistoryFragment: TipHistoryFragment,
        tipDetailsNavValues: TipDetailsNavValues
    ) {
        val action = TipHistoryFragmentDirections.actionTipHistoryFragmentToTipDetailsFragment(
            tipDetailsNavValues
        )

        tipHistoryFragment.navigate(action)
    }
}