package com.example.plantRecognitionApplication.photo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoViewModel : ViewModel() {
    private val _bitmapPhoto = MutableLiveData<Bitmap>()
    private val _photoFlag = MutableLiveData<Boolean>()
    private val _pathToPhoto = MutableLiveData<String>()
    private val _exception = MutableLiveData<String>()
    private val _photoURI = MutableLiveData<Uri>()
    val bitmap_photo: LiveData<Bitmap>
        get() = _bitmapPhoto
    val photo_flag: LiveData<Boolean>
        get() = _photoFlag
    val pathToPhoto: LiveData<String>
        get() = _pathToPhoto
    val exception: LiveData<String>
        get() = _exception
    val photoURI: LiveData<Uri>
        get() = _photoURI

    init {
        Log.i("GameViewModel", "GameViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }


    @Throws(IOException::class)
    private fun createImageFile(context: Context?): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            _pathToPhoto.value = absolutePath
        }
    }



    fun dispatchTakePictureIntent(context: Context) {
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    _exception.value = "Something went wrong"
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.example.android.fileprovider",
                        it
                    )
                    _photoURI.value= photoURI
                    _photoFlag.value = true
                }
            }
}









/*
   fun rotateimage() {
        val imageFile = File(pathToPhoto.value)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        var rotate: Float = 0.0F
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = -90.0F
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180.0F
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90.0F
        }
        var bitmap = BitmapFactory.decodeFile(pathToPhoto.value)
        bitmap = bitmap.rotate(rotate)
        _bitmap_photo.value = bitmap

        }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

 */


