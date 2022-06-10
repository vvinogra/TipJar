package com.example.tipjar.shared.ui.extensions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

private const val DATA_KEY = "data"

fun <T> SavedStateHandle.getArgsLiveData(): MutableLiveData<T> = getLiveData(DATA_KEY)
fun <T> SavedStateHandle.getArgs(): T = get<T>(DATA_KEY)!!
fun <T> SavedStateHandle.getArgsNullable(): T? = get<T>(DATA_KEY)
