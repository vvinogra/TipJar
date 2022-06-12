package com.example.tipjar.shared.ui.util.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class OpenCameraContract : ActivityResultContract<Uri, Boolean>() {
    override fun createIntent(context: Context, input: Uri): Intent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}