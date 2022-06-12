package com.example.tipjar.data.tiphistory

import android.net.Uri
import com.example.tipjar.data.image.IImageStorageManager
import com.example.tipjar.data.tiphistory.local.TipHistoryLocalDataSource
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TipHistoryRepository @Inject constructor(
    private val tipHistoryLocalDataSource: TipHistoryLocalDataSource,
    private val iImageStorageManager: IImageStorageManager,
    coroutineScope: CoroutineScope
) : ITipHistoryRepository {

    private val currentTimeInMillis: Long
        get() = Calendar.getInstance().timeInMillis

    init {
        coroutineScope.launch {
            clearRemovedImages()
        }
    }

    override suspend fun createTipHistoryRecord(
        totalAmount: Double,
        tipAmount: Double,
        currencyCode: String,
        receiptImageUri: Uri?
    ) {
        val receiptImageFilename = receiptImageUri?.lastPathSegment

        tipHistoryLocalDataSource.create(
            TipHistoryEntity(
                id = 0,
                totalAmount = totalAmount,
                tipAmount = tipAmount,
                currencyCode = currencyCode,
                receiptImageFilename = receiptImageFilename,
                timestamp = currentTimeInMillis
            )
        )

        if (receiptImageFilename != null) {
            iImageStorageManager.saveImage(receiptImageUri, receiptImageFilename)
        }
    }

    override fun createUriToSaveReceiptImage(): Uri? {
        return iImageStorageManager.createUriToSaveOriginalImage()
    }

    override suspend fun restoreTipHistoryEntity(entity: TipHistoryEntity) {
        tipHistoryLocalDataSource.create(entity)
    }

    override fun getReceiptImagePath(entity: TipHistoryEntity): String? {
        return entity.receiptImageFilename?.let { iImageStorageManager.getImagePath(it) }
    }

    override fun getReceiptThumbImagePath(entity: TipHistoryEntity): String? {
        return entity.receiptImageFilename?.let { iImageStorageManager.getThumbnailImagePath(it) }
    }

    override suspend fun removeTipHistoryRecord(entity: TipHistoryEntity) {
        tipHistoryLocalDataSource.removeById(entity.id)
    }

    override suspend fun getAllTipHistoryRecords(): List<TipHistoryEntity> {
        return tipHistoryLocalDataSource.getAll()
    }

    private suspend fun clearRemovedImages() {
        val tipHistoryReceiptImageFilenames = getAllTipHistoryRecords()
            .mapNotNull { it.receiptImageFilename }
        val allSavedImageFilenames = iImageStorageManager.getAllImageFilenames()

        allSavedImageFilenames.forEach {
            if (!tipHistoryReceiptImageFilenames.contains(it)) {
                iImageStorageManager.removeImageIfExists(it)
            }
        }
    }
}