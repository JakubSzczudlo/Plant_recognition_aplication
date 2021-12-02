package com.example.plantRecognitionApplication.photo

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.DateFormat.getDateInstance
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import android.R.attr.data
import android.graphics.BitmapFactory
import android.provider.MediaStore.Images.Thumbnails.getThumbnail
import android.provider.MediaStore





class PhotoViewModel : ViewModel() {
    private val _bitmapPhoto = MutableLiveData<Bitmap>()
    private val _photoFlag = MutableLiveData<Boolean>()
    private val _pathToPhoto = MutableLiveData<String>()
    private val _exception = MutableLiveData<String>()
    private val _photoURI = MutableLiveData<Uri>()
    private val _recflag = MutableLiveData<Boolean>()
    val bitmapPhoto: LiveData<Bitmap>
        get() = _bitmapPhoto
    val photoFlag: LiveData<Boolean>
        get() = _photoFlag
    val pathToPhoto: LiveData<String>
        get() = _pathToPhoto
    val exception: LiveData<String>
        get() = _exception
    val photoURI: LiveData<Uri>
        get() = _photoURI
    val recflag: LiveData<Boolean>
        get() = _recflag

    init {
        Log.i("GameViewModel", "GameViewModel created!")

    }
    fun photoViewed(){
        _photoFlag.value = false
        _recflag.value = true
    }



    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")

    }


    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = getDateInstance().toString()
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

    fun makeRecognitions(classifier: Classifier,context: Context): String{
        if(photoFlag.value == true) {
            var bitmap = BitmapFactory.decodeFile(pathToPhoto.value)

            val recognitions = classifier.recognize(bitmap)
            val txt = recognitions.joinToString(separator = "\n")
            return txt
        }
        return "Mistake"
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


