package com.example.plant_recognition_aplication.menu

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.plant_recognition_aplication.R
import com.example.plant_recognition_aplication.databinding.FragmentMenuBinding



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


