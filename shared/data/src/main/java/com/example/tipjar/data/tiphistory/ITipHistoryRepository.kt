package com.example.tipjar.data.tiphistory

import android.net.Uri
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity

interface ITipHistoryRepository {
    suspend fun createTipHistoryRecord(
        totalAmount: Double,
        tipAmount: Double,
        currencyCode: String,
        receiptImageUri: Uri? = null
    )
    fun createUriToSaveReceiptImage(): Uri?
    suspend fun restoreTipHistoryEntity(entity: TipHistoryEntity)
    fun getReceiptImagePath(entity: TipHistoryEntity): String?
    fun getReceiptThumbImagePath(entity: TipHistoryEntity): String?
    suspend fun removeTipHistoryRecord(entity: TipHistoryEntity)
    suspend fun getAllTipHistoryRecords(): List<TipHistoryEntity>
}