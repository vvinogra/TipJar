package com.example.tipjar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tip_history")
data class TipHistoryDb(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "total_amount") val totalAmount: Int,
    @ColumnInfo(name = "tip_amount") val tipAmount: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)