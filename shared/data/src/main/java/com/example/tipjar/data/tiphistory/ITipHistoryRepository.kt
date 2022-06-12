package com.example.tipjar.data.tiphistory

import android.graphics.Bitmap
import android.net.Uri
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

interface ITipHistoryRepository {
    fun createTipHistoryRecord(
        totalAmount: Double,
        tipAmount: Double,
        currencyCode: String,
        receiptImageUri: Uri? = null
    )
    fun createUriToSaveReceiptImage(): Uri?
    fun restoreTipHistoryEntity(entity: TipHistoryEntity, receiptBitmap: Bitmap?)
    fun getReceiptImagePath(entity: TipHistoryEntity): String?
    fun getReceiptThumbImagePath(entity: TipHistoryEntity): String?
    fun getFullSizedReceiptImage(entity: TipHistoryEntity): Bitmap?
    fun removeTipHistoryRecord(entity: TipHistoryEntity)
    fun getAllTipHistoryRecords(): List<TipHistoryEntity>
}