package com.example.tipjar.data.tiphistory

import android.graphics.Bitmap
import com.example.tipjar.data.image.IImageStorageManager
import com.example.tipjar.data.tiphistory.local.TipHistoryLocalDataSource
import com.example.tipjar.data.tiphistory.local.model.CreateTipHistoryEntityDto
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TipHistoryRepository @Inject constructor(
    private val tipHistoryLocalDataSource: TipHistoryLocalDataSource,
    private val iImageStorageManager: IImageStorageManager
) : ITipHistoryRepository {
    override fun createTipHistoryRecord(
        totalAmount: Double,
        tipAmount: Double,
        currencyCode: String,
        receiptBitmap: Bitmap?
    ) {
        val savedId = tipHistoryLocalDataSource.create(
            CreateTipHistoryEntityDto(totalAmount, tipAmount, Calendar.getInstance().timeInMillis, currencyCode)
        )

        if (receiptBitmap != null) {
            iImageStorageManager.saveImage(receiptBitmap, savedId.toString())
        }
    }

    override fun getTipHistoryImagePathById(id: Int): String? {
        return iImageStorageManager.getImagePath(id.toString())
    }

    override fun removeTipHistoryRecordById(id: Int) {
        tipHistoryLocalDataSource.removeById(id)
        iImageStorageManager.removeImageIfExists(id.toString())
    }

    override fun getAllTipHistoryRecords(): List<TipHistoryEntity> {
        return tipHistoryLocalDataSource.getAll()
    }

}