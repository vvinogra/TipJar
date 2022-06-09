package com.example.tipjar.data.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.webkit.MimeTypeMap
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
//
//    override fun createCopy(originalId: String, newId: String) {
//        val originalImageFile = getImageFile(originalId)
//        val originalThumbnailImageFile = getThumbnailImageFile(originalId)
//        if (originalImageFile.exists()) {
//            val newImageFile = getImageFile(newId)
//            val newThumbnailImageFile = getThumbnailImageFile(newId)
//            originalImageFile.createFileCopy(newImageFile)
//            originalThumbnailImageFile.createFileCopy(newThumbnailImageFile)
//        }
//    }
//
//    override fun saveImageData(id: String, cachedImage: CachedImage?, currentImage: ImageData) {
//        if (isImageChanged(cachedImage, currentImage)) {
//            when (cachedImage?.imageData) {
//                is ImageData.ResImageData -> {
//                    getImageFile(id).delete()
//                    getThumbnailImageFile(id).delete()
//                }
//                is ImageData.FileImageData -> {
//                    val imageFile = getImageFile(id)
//                    val thumbnailImageFile = getThumbnailImageFile(id)
//                    saveBitmapToFile(
//                        cachedImage.bitmap!!,
//                        Bitmap.CompressFormat.JPEG,
//                        imageFile,
//                        thumbnailImageFile
//                    )
//                }
//            }
//        }
//    }

//    override fun isImageChanged(
//        newImage: CachedImage?,
//        originalImage: ImageData
//    ): Boolean {
//        val newImageData = newImage?.imageData
//        return when {
//            newImageData is ImageData.ResImageData && originalImage is ImageData.FileImageData -> true
//            newImageData is ImageData.FileImageData && originalImage is ImageData.FileImageData -> {
//                val newBitmap = newImage.bitmap
//                val originalBitmap = loadBitmapFromPath(originalImage.path)
//                !originalBitmap.sameAs(newBitmap)
//            }
//            else -> false
//        }
//    }
//
//    override fun getBitmap(imageData: ImageData?): Bitmap? = when (imageData) {
//        is ImageData.FileImageData -> loadBitmapFromPath(imageData.path)
//        else -> null
//    }
//
//    private fun loadBitmapFromPath(path: String): Bitmap =
//        BitmapFactory.decodeFile(path, BitmapFactory.Options())
//
//    private fun File.createFileCopy(dest: File) {
//        FileInputStream(this).use { fis ->
//            val buffer = ByteArray(1024)
//            FileOutputStream(dest).use { fos ->
//                var length: Int
//                while (fis.read(buffer).also { length = it } > 0) {
//                    fos.write(buffer, 0, length)
//                }
//            }
//        }
//    }
//
    private fun File.saveToFile(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat,
        imageQuality: Int
    ) {
        FileOutputStream(this).use {
            bitmap.compress(compressFormat, imageQuality, it)
        }
    }
//
//    private fun getCompressFormat(uri: Uri): Bitmap.CompressFormat =
//        if (getFileExtension(uri) == IMAGE_EXT_PNG) {
//            Bitmap.CompressFormat.PNG
//        } else {
//            Bitmap.CompressFormat.JPEG
//        }
//
//    private fun getFileExtension(uri: Uri): String? {
//        val extension: String?
//        val contentResolver: ContentResolver = applicationContext.contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
//        return extension
//    }
//
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
//
//    private fun getBitmapFromUri(uri: Uri): Bitmap? {
//        val parcelFileDescriptor = applicationContext.contentResolver.openFileDescriptor(uri, "r")
//        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
//        val image: Bitmap? = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//        val orientedImage = image.modifyOrientation(fileDescriptor)
//        parcelFileDescriptor?.close()
//        return orientedImage
//    }
//
//    private fun Bitmap?.modifyOrientation(fileDescriptor: FileDescriptor?): Bitmap? {
//        if (this == null || fileDescriptor == null) return null
//        val exifInterface = ExifInterface(fileDescriptor)
//        return when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(degrees = 90)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(degrees = 180)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(degrees = 270)
//            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(FlipType.HORIZONTAL)
//            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(FlipType.VERTICAL)
//            else -> this
//        }
//    }
//
//    private fun Bitmap.rotate(degrees: Int): Bitmap {
//        val matrix = Matrix()
//        matrix.postRotate(degrees.toFloat())
//        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
//    }
//
//    private fun Bitmap.flip(flipType: FlipType): Bitmap {
//        val matrix = Matrix()
//        when (flipType) {
//            FlipType.HORIZONTAL -> matrix.preScale(-1f, 1f)
//            FlipType.VERTICAL -> matrix.preScale(1f, -1f)
//        }
//        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
//    }
//
//    enum class FlipType {
//        HORIZONTAL, VERTICAL
//    }
}

sealed class ImageSavingResult {
    object Error : ImageSavingResult()
    data class Success(val path: String) : ImageSavingResult()
}
