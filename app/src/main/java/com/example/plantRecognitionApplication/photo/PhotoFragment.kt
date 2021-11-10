package com.example.plantRecognitionApplication.photo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.plantRecognitionApplication.R
import com.example.plantRecognitionApplication.databinding.FragmentPhotoBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer



class PhotoFragment : Fragment() {

    private lateinit var viewModel: PhotoViewModel

    private lateinit var binding: FragmentPhotoBinding

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            binding.imageView.setImageURI(viewModel.photoURI.value)
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


        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.photo_flag.observe(viewLifecycleOwner, Observer { photoTaken ->
            if(photoTaken) {
                takeImageResult.launch(viewModel.photoURI.value)
            }
        })

        binding.takeImageButton.setOnClickListener { v: View ->
            if (context != null) {
                viewModel.dispatchTakePictureIntent(context)
            }
        }


        return binding.root
    }



}