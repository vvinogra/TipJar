package com.example.tipjar.core.ui.tipsplitter.model

import android.net.Uri

sealed class TipSplitterNavigation {
    object TipHistory: TipSplitterNavigation()
    data class TakePhotoOfReceipt(val uri: Uri): TipSplitterNavigation()
}