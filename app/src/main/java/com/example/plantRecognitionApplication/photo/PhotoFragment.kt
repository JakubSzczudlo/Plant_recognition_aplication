package com.example.plantRecognitionApplication.photo

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.plantRecognitionApplication.R
import com.example.plantRecognitionApplication.databinding.FragmentPhotoBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.plantRecognitionApplication.MainActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.schema.TensorType.FLOAT16
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


const val INPUT_SIZE: Int = 224
const val BYTES_PER_CHANNEL: Int = 4
const val PIXEL_SIZE: Int = 3
const val BATCH_SIZE: Int = 1



class PhotoFragment : Fragment() {

    //private lateinit var classifier: Classifier

    //private lateinit var assetManager: AssetManager


    private lateinit var viewModel: PhotoViewModel

    private lateinit var binding: FragmentPhotoBinding

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                binding.imageView.setImageURI(viewModel.photoURI.value)
                viewModel.photoViewed()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_photo, container, false
        )
        val context: Context? = activity

        viewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        val assetManager = context!!.resources.assets
        val classifier = Classifier(assetManager)

        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.photoFlag.observe(viewLifecycleOwner, Observer { photoTaken ->
            if (photoTaken) {
                takeImageResult.launch(viewModel.photoURI.value)

            }
        })
        viewModel.recflag.observe(viewLifecycleOwner, Observer { recDone ->
            if (recDone) {
                var bitmap = BitmapFactory.decodeFile(viewModel.pathToPhoto.value)
                val recognitions = classifier.recognize(bitmap)
                val txt = recognitions.joinToString(separator = "\n")
                //Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
                binding.results.text = txt
                binding.ResultTitle.visibility = View.VISIBLE

            }


        })
        viewModel.exception.observe(viewLifecycleOwner, Observer { except ->
            if (except != null) {
                Toast.makeText(activity, except, Toast.LENGTH_SHORT).show()
            }
        })

        binding.takeImageButton.setOnClickListener { v: View ->
            if (context != null) {
                viewModel.dispatchTakePictureIntent(context)
            }
        }

        return binding.root
    }




/*
    fun TfModel(context: Context, bitmap: Bitmap) {
        val model = Model.newInstance(context)


// Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

// Releases model resources if no longer used.
        model.close()
*/








}