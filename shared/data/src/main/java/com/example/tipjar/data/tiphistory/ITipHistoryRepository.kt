package com.example.tipjar.data.tiphistory

import android.graphics.Bitmap
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

interface ITipHistoryRepository {
    fun createTipHistoryRecord(
        totalAmount: Double,
        tipAmount: Double,
        currencyCode: String,
        receiptBitmap: Bitmap? = null
    )
    fun restoreTipHistoryEntity(entity: TipHistoryEntity, receiptBitmap: Bitmap?)
    fun getTipHistoryImagePathById(id: Int): String?
    fun removeTipHistoryRecordById(id: Int)
    fun getAllTipHistoryRecords(): List<TipHistoryEntity>
}