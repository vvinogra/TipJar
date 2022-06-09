package com.example.tipjar.core.ui.tipsplitter.model

sealed class TipSplitterNavigation {
    object TipHistory: TipSplitterNavigation()
    object TakePhotoOfReceipt: TipSplitterNavigation()
}