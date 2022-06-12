package com.example.tipjar.data.tiphistory.local

import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import com.example.tipjar.database.TipDatabase
import com.example.tipjar.database.dao.TipHistoryDao
import com.example.tipjar.database.entity.TipHistoryDbEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TipHistoryLocalDataSource @Inject constructor(
    private val tipDatabase: TipDatabase
) {

    private val tipHistoryDao: TipHistoryDao
        get() = tipDatabase.tipHistoryDao()

    fun getAll(): List<TipHistoryEntity> = tipHistoryDao.getAll().map { it.asTipHistoryEntity() }

    fun create(tipHistoryEntity: TipHistoryEntity): Int {
        return tipHistoryDao.insert(tipHistoryEntity.asTipHistoryDbEntity()).toInt()
    }

    fun removeById(id: Int) {
        tipHistoryDao.removeById(id)
    }

    private fun TipHistoryEntity.asTipHistoryDbEntity(): TipHistoryDbEntity {
        return TipHistoryDbEntity(
            id = id,
            totalAmount = totalAmount,
            tipAmount = tipAmount,
            timestamp = timestamp,
            receiptImageFilename = receiptImageFilename,
            currencyCode = currencyCode
        )
    }

    private fun TipHistoryDbEntity.asTipHistoryEntity(): TipHistoryEntity {
        return TipHistoryEntity(
            id = id,
            totalAmount = totalAmount,
            tipAmount = tipAmount,
            timestamp = timestamp,
            receiptImageFilename = receiptImageFilename,
            currencyCode = currencyCode
        )
    }
}