package com.example.plantRecognitionApplication.photo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.plantRecognitionApplication.R
import com.example.plantRecognitionApplication.databinding.FragmentPhotoBinding
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val REQUEST_IMAGE_CAPTURE =42
private val bitmap_photo = MutableLiveData<Bitmap>()
private val photo_flag = MutableLiveData<Boolean>()
private val pathToPhoto = MutableLiveData<String>()


class PhotoFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPhotoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_photo, container, false)
        val context: Context? = activity

        binding.takeImageButton.setOnClickListener { v: View ->
            //Toast.makeText(activity, "dziala?", Toast.LENGTH_SHORT).show()
            photo_flag.value = false
            dispatchTakePictureIntent(context)

        }
        binding.viewPhotoButton.setOnClickListener{
            if(photo_flag.value == true)
                binding.imageView.setImageBitmap(bitmap_photo.value)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* If the intent was induced the photo is saved in file and MutableLiveData" */
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            val imageFile = File(pathToPhoto.value)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            var rotate : Float = 0.0F
            when(orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270.0F
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180.0F
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90.0F
            }
            var bitmap = BitmapFactory.decodeFile(pathToPhoto.value)
            bitmap = bitmap.rotate(rotate)
            bitmap_photo.value = bitmap

        }

    }

    private fun checkingCamera(context: Context?, activity : FragmentActivity?){
        /* Test if the given smartphone have built-in camera*/
        val packageManager = context?.packageManager
        if (!packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)!!) {
            Toast.makeText(activity, "This device does not have a camera.", Toast.LENGTH_SHORT)
                .show()
            return
        }
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
            pathToPhoto.value = absolutePath
        }
    }

    private fun dispatchTakePictureIntent(context: Context?) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    photo_flag.value= true
                }
            }
        }
    }
    /*
    private fun setPic(binding: FragmentPhotoBinding) {
        BitmapFactory.decodeFile(pathToPhoto.value)?.also { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        }
    }
    */

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


}