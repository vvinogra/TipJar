package com.example.tipjar.core.navigation

import com.example.tipjar.core.ui.tipsplitter.TipSplitterFragment

interface CoreNavigation {
    fun fromTipSplitterToTipHistory(tipSplitterFragment: TipSplitterFragment)
}