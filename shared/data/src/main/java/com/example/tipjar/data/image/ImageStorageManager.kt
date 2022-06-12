package com.example.tipjar.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val AUTHORITY = "com.example.tipjar.data.provider"

private const val IMAGES_FOLDER = "ReceiptImages"
private const val THUMBNAIL_IMAGES_FOLDER = "Thumbs"
private const val IMAGE_QUALITY = 100
private const val THUMBNAIL_IMAGE_QUALITY = 70
private const val THUMB_WIDTH = 128
private const val THUMB_HEIGHT = 128

private val BITMAP_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG

@Singleton
internal class ImageStorageManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : IImageStorageManager {

    override fun createUriToSaveOriginalImage(): Uri? {
        return try {
            val file = createImageFile() ?: return null

            return FileProvider.getUriForFile(
                applicationContext,
                AUTHORITY,
                file
            )
        } catch (exception: IOException) {
            null
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat(
            "yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val imagesStorageDir = getImagesFolder()

        val prefix = "JPEG_${timeStamp}_"
        val suffix = ".jpg"

        return File.createTempFile(
            prefix,
            suffix,
            imagesStorageDir
        )
    }

    override fun saveImage(bitmap: Bitmap, imageName: String): ImageSavingResult {
        return try {
            val receiptImageFile = getImageFile(imageName)
            val receiptThumbnailImageFile = getThumbnailImageFile(imageName)

            val compressFormat = BITMAP_COMPRESS_FORMAT
            saveBitmapToFile(bitmap, compressFormat, receiptImageFile, receiptThumbnailImageFile)

            if (receiptImageFile.exists() && receiptImageFile.length() > 0) {
                ImageSavingResult.Success(receiptImageFile.absolutePath)
            } else {
                ImageSavingResult.Error
            }
        } catch (exception: IOException) {
            ImageSavingResult.Error
        }
    }

    override fun saveImage(uri: Uri, imageName: String): ImageSavingResult {
        return try {
            getBitmapFromUri(uri)?.let { bitmap ->
                saveImage(bitmap, imageName)
            } ?: ImageSavingResult.Error
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
        val appRootFolder = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val receiptImageFolder = File("$appRootFolder/$IMAGES_FOLDER")
        if (createIfNotExists && !receiptImageFolder.exists()) {
            receiptImageFolder.mkdir()
        }
        return receiptImageFolder
    }

    private fun getThumbnailsImageFolder(): File {
        val receiptFolder = getImagesFolder()
        val receiptThumbnailImageFolder = File("$receiptFolder/$THUMBNAIL_IMAGES_FOLDER")
        if (!receiptThumbnailImageFolder.exists()) {
            receiptThumbnailImageFolder.mkdir()
        }
        return receiptThumbnailImageFolder
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = applicationContext.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap? = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        val orientedImage = image.modifyOrientation(fileDescriptor)
        parcelFileDescriptor?.close()
        return orientedImage
    }

    private fun Bitmap?.modifyOrientation(fileDescriptor: FileDescriptor?): Bitmap? {
        if (this == null || fileDescriptor == null) return null
        val exifInterface = ExifInterface(fileDescriptor)
        return when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(degrees = 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(degrees = 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(degrees = 270)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(FlipType.HORIZONTAL)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(FlipType.VERTICAL)
            else -> this
        }
    }

    private fun Bitmap.rotate(degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.flip(flipType: FlipType): Bitmap {
        val matrix = Matrix()
        when (flipType) {
            FlipType.HORIZONTAL -> matrix.preScale(-1f, 1f)
            FlipType.VERTICAL -> matrix.preScale(1f, -1f)
        }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private enum class FlipType {
        HORIZONTAL, VERTICAL
    }
}

sealed class ImageSavingResult {
    object Error : ImageSavingResult()
    data class Success(val path: String) : ImageSavingResult()
}
