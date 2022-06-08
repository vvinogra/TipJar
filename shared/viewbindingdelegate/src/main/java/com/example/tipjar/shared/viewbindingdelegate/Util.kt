package com.example.tipjar.shared.viewbindingdelegate

import android.os.Looper

internal fun checkIsMainThread() = check(isMainThread)

internal inline val isMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()