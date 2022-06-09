package com.example.tipjar.data.tiphistory

import android.graphics.Bitmap
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

interface ITipHistoryRepository {
    fun createTipHistoryRecord(totalAmount: Double, tipAmount: Double, receiptBitmap: Bitmap? = null)
    fun removeTipHistoryRecordById(id: Int)
    fun getAllTipHistoryRecords(): List<TipHistoryEntity>
}