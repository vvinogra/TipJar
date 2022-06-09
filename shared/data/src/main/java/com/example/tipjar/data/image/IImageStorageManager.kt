package com.example.tipjar.data.image

import android.graphics.Bitmap

interface IImageStorageManager {
    fun saveImage(bitmap: Bitmap, imageName: String): ImageSavingResult
    fun getImagePath(id: String): String?
    fun getThumbnailImagePath(id: String): String?
    fun removeImageIfExists(id: String)
    fun clear()
}