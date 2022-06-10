package com.example.tipjar.core.ui.tipdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.shared.ui.extensions.getArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TipDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _tipDetailsUiData = MutableStateFlow<TipDetailsNavValues>(savedStateHandle.getArgs())
    val tipDetailsUiData = _tipDetailsUiData.asStateFlow()
}