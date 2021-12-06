package com.example.plantRecognitionApplication.photo

import kotlin.math.roundToInt

data class Recognition(
    val name: String,
    val probability: Float
) {
    override fun toString() =
        "$name : ${((probability * 10000.0).roundToInt()/100.0)}%"
}
