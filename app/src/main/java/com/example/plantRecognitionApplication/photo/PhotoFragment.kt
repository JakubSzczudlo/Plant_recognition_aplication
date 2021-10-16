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
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photo_flag.value = false
            checkingCamera(context,activity)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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
            val imageBitmap = data?.extras?.get("data") as Bitmap
            bitmap_photo.value = imageBitmap
            photo_flag.value = true
            //bitmapToFile(imageBitmap,"Test")
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



/*
    private fun bitmapToFile(imageBitmap: Bitmap, fileNameToSave: String): File? {
        /* Saving bitmap to file for later use */
        var file: File? = null
        //Toast.makeText(activity, "Worked", Toast.LENGTH_SHORT).show()
        return try {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave
            pathToPhoto.value = path
            file = File(path)
            file.createNewFile()

            //Convert bitmap to byte array
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitmapdata = stream.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file
        }
    }

 */

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
            // Save a file: path for use with ACTION_VIEW intents
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
                }
            }
        }
    }




/*
private fun savingPhoto(){
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

}

lateinit var currentPhotoPath: String

@Throws(IOException::class)
private fun createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
}

private fun dispatchTakePictureIntent(context: Context?,activity : FragmentActivity?) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        activity?.let {
            takePictureIntent.resolveActivity(it.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(activity, "Not working", Toast.LENGTH_SHORT)
                        .show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri? = context?.let { it1 ->
                        FileProvider.getUriForFile(
                            it1,
                            "com.example.android.fileprovider",
                            it
                        )
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}
*/
}