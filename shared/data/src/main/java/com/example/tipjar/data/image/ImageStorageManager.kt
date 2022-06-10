package com.example.tipjar.data.image

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton

private const val IMAGES_FOLDER = "TipJarImages"
private const val THUMBNAIL_IMAGES_FOLDER = "Thumbs"
private const val IMAGE_QUALITY = 100
private const val THUMBNAIL_IMAGE_QUALITY = 70
private const val THUMB_WIDTH = 53
private const val THUMB_HEIGHT = 53

@Singleton
internal class ImageStorageManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : IImageStorageManager {
    override fun saveImage(bitmap: Bitmap, imageName: String): ImageSavingResult {
        return try {
            val sceneImageFile = getImageFile(imageName)
            val sceneThumbnailImageFile = getThumbnailImageFile(imageName)

            val compressFormat = Bitmap.CompressFormat.PNG
            saveBitmapToFile(bitmap, compressFormat, sceneImageFile, sceneThumbnailImageFile)

            if (sceneImageFile.exists() && sceneImageFile.length() > 0) {
                ImageSavingResult.Success(sceneImageFile.absolutePath)
            } else {
                ImageSavingResult.Error
            }
        } catch (exception: IOException) {
            ImageSavingResult.Error
        }
    }

    private fun saveBitmapToFile(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat,
        imageFile: File,
        thumbnailImageFile: File
    ) {
        imageFile.removeIfExist()
        thumbnailImageFile.removeIfExist()
        imageFile.saveToFile(bitmap, compressFormat, IMAGE_QUALITY)
        val thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, THUMB_WIDTH, THUMB_HEIGHT)
        thumbnailImageFile.saveToFile(thumbnailBitmap, compressFormat, THUMBNAIL_IMAGE_QUALITY)
    }

    override fun getImagePath(id: String): String? =
        getImageFile(id).getFilePathIfExist()

    override fun getThumbnailImagePath(id: String): String? =
        getThumbnailImageFile(id).getFilePathIfExist()

    override fun removeImageIfExists(id: String) {
        val imageFile = getImageFile(id)
        val thumbnailImageFile = getThumbnailImageFile(id)
        imageFile.removeIfExist()
        thumbnailImageFile.removeIfExist()
    }

    override fun clear() {
        val imagesFolder = getImagesFolder(createIfNotExists = false)

        if (imagesFolder.exists()) {
            imagesFolder.deleteRecursively()
        }
    }

    private fun File.saveToFile(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat,
        imageQuality: Int
    ) {
        FileOutputStream(this).use {
            bitmap.compress(compressFormat, imageQuality, it)
        }
    }

    private fun File.getFilePathIfExist(): String? =
        if (exists()) {
            absolutePath
        } else {
            null
        }

    private fun getImageFile(id: String) = File(getImagesFolder().absolutePath, id)

    private fun getThumbnailImageFile(id: String) =
        File(getThumbnailsImageFolder().absolutePath, id)

    private fun File.removeIfExist() {
        if (exists()) {
            delete()
        }
    }

    private fun getImagesFolder(createIfNotExists: Boolean = true): File {
        val appRootFolder = applicationContext.filesDir
        val sceneImageFolder = File("$appRootFolder/$IMAGES_FOLDER}")
        if (createIfNotExists && !sceneImageFolder.exists()) {
            sceneImageFolder.mkdir()
        }
        return sceneImageFolder
    }

    private fun getThumbnailsImageFolder(): File {
        val sceneFolder = getImagesFolder()
        val sceneThumbnailImageFolder = File("$sceneFolder/$THUMBNAIL_IMAGES_FOLDER")
        if (!sceneThumbnailImageFolder.exists()) {
            sceneThumbnailImageFolder.mkdir()
        }
        return sceneThumbnailImageFolder
    }
}

sealed class ImageSavingResult {
    object Error : ImageSavingResult()
    data class Success(val path: String) : ImageSavingResult()
}
