package com.example.tipjar.core.ui.tipsplitter.model

import android.net.Uri

sealed class TipSplitterNavigation {
    object TipHistory: TipSplitterNavigation()
    object ChangeCurrency: TipSplitterNavigation()
    data class TakePhotoOfReceipt(val uri: Uri): TipSplitterNavigation()
}