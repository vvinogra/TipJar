package com.example.tipjar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tipjar.database.Tables

@Entity(tableName = Tables.TIP_HISTORY)
data class TipHistoryDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "tip_amount") val tipAmount: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)