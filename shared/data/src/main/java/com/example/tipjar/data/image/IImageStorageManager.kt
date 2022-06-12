package com.example.tipjar.data.image

import android.graphics.Bitmap
import android.net.Uri

internal interface IImageStorageManager {
    fun createUriToSaveOriginalImage(): Uri?
    fun saveImage(bitmap: Bitmap, imageName: String): ImageSavingResult
    fun saveImage(uri: Uri, imageName: String): ImageSavingResult
    fun getImagePath(id: String): String?
    fun getThumbnailImagePath(id: String): String?
    fun removeImageIfExists(id: String)
    fun getAllImageFilenames(): Set<String>
    fun clear()
}