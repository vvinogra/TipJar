package com.example.tipjar.core.util.activityresult

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class OpenCameraContract : ActivityResultContract<Unit, Bitmap?>() {
    override fun createIntent(context: Context, input: Unit): Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
        val extras: Bundle? = intent?.extras

        return extras?.get("data") as Bitmap?
    }
}