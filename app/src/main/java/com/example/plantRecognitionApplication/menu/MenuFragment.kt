package com.example.plantRecognitionApplication.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.plantRecognitionApplication.R
import com.example.plantRecognitionApplication.databinding.FragmentMenuBinding



class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMenuBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_menu, container, false)


        binding.startButton.setOnClickListener { v: View ->
            v.findNavController().navigate(R.id.action_menuFragment_to_photoFragment)
        }
        binding.aboutButton.setOnClickListener { v: View ->
            v.findNavController().navigate(R.id.action_menuFragment_to_aboutFragment)
        }
        return binding.root
    }


//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        when (currentNightMode) {
//            Configuration.UI_MODE_NIGHT_NO -> {.value = false} // Night mode is not active, we're using the light theme
//            Configuration.UI_MODE_NIGHT_YES -> {.value = true} // Night mode is active, we're using dark theme
//
//
//        }
//    }
}


