package com.example.tipjar.data.tiphistory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.tipjar.data.image.IImageStorageManager
import com.example.tipjar.data.tiphistory.local.TipHistoryLocalDataSource
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TipHistoryRepository @Inject constructor(
    private val tipHistoryLocalDataSource: TipHistoryLocalDataSource,
    private val iImageStorageManager: IImageStorageManager
) : ITipHistoryRepository {

    private val currentTimeInMillis: Long
        get() = Calendar.getInstance().timeInMillis

    override fun createTipHistoryRecord(
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

    override fun restoreTipHistoryEntity(entity: TipHistoryEntity, receiptBitmap: Bitmap?) {
        tipHistoryLocalDataSource.create(entity)

        if (receiptBitmap != null && entity.receiptImageFilename != null) {
            iImageStorageManager.saveImage(receiptBitmap, entity.receiptImageFilename)
        }
    }

    override fun getReceiptImagePath(entity: TipHistoryEntity): String? {
        return entity.receiptImageFilename?.let { iImageStorageManager.getImagePath(it) }
    }

    override fun getReceiptThumbImagePath(entity: TipHistoryEntity): String? {
        return entity.receiptImageFilename?.let { iImageStorageManager.getThumbnailImagePath(it) }
    }

    override fun getFullSizedReceiptImage(entity: TipHistoryEntity): Bitmap? {
        if (entity.receiptImageFilename == null)
            return null

        return iImageStorageManager.getImagePath(entity.receiptImageFilename)?.let {
            BitmapFactory.decodeFile(it)
        }
    }

    override fun removeTipHistoryRecord(entity: TipHistoryEntity) {
        tipHistoryLocalDataSource.removeById(entity.id)

        if (entity.receiptImageFilename != null) {
            iImageStorageManager.removeImageIfExists(entity.receiptImageFilename)
        }
    }

    override fun getAllTipHistoryRecords(): List<TipHistoryEntity> {
        return tipHistoryLocalDataSource.getAll()
    }

}