package com.example.tipjar.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tipjar.database.Tables
import com.example.tipjar.database.entity.TipHistoryDbEntity

@Dao
interface TipHistoryDao {
    @Query("SELECT * FROM ${Tables.TIP_HISTORY}")
    suspend fun getAll(): List<TipHistoryDbEntity>

    @Insert
    suspend fun insert(tipHistoryDbEntity: TipHistoryDbEntity): Long

    @Query("DELETE FROM ${Tables.TIP_HISTORY} WHERE id = :id")
    suspend fun removeById(id: Int)
}