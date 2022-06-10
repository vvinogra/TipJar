package com.example.tipjar.core.ui.tipdetails.navigation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TipDetailsNavValues(
    val date: String,
    val totalAmount: String,
    val tipTotalAmount: String,
    val imagePath: String
) : Parcelable