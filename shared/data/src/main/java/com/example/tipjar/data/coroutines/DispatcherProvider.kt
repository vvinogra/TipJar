package com.example.tipjar.data.coroutines

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

interface DispatcherProvider {
    val main
        get() = Dispatchers.Main
    val default
        get() = Dispatchers.Default
    val io
        get() = Dispatchers.IO
    val unconfined
        get() = Dispatchers.Unconfined
}

@Singleton
internal class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider