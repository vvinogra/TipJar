package com.example.tipjar.navigation.core

import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CoreNavigationImpl @Inject constructor(): CoreNavigation {
    override fun fromTipSplitterToSavedPayments(tipSplitterFragment: TipSplitterFragment) {
        // TODO
    }
}