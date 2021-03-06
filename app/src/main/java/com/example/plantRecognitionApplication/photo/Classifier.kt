package com.example.plantRecognitionApplication.photo

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*

class Classifier(assetManager: AssetManager) {

    private val labels: List<String>
    private val model: Interpreter

    init {
        model = Interpreter(getModel(assetManager, MODEL_PATH))
        labels = getLabels(assetManager, LABELS_PATH)
    }
    /*Function responsible for generating recognitions from given bitmap*/
    fun recognize(first_bitmap: Bitmap): List<Recognition> {
        val result = Array(BATCH_SIZE) { FloatArray(labels.size) }

        val bitmap = Bitmap.createScaledBitmap(first_bitmap, MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, false)

        val byteBuffer = ByteBuffer
            .allocateDirect(
                BATCH_SIZE *
                        MODEL_INPUT_SIZE *
                        MODEL_INPUT_SIZE *
                        BYTES_PER_CHANNEL *
                        PIXEL_SIZE
            )
            .apply { order(ByteOrder.nativeOrder()) }

        val pixelValues = IntArray(MODEL_INPUT_SIZE * MODEL_INPUT_SIZE)
        bitmap.getPixels(pixelValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        /* Canal alfa deleting*/
        var pixel = 0
        for (i in 0 until MODEL_INPUT_SIZE) {
            for (j in 0 until MODEL_INPUT_SIZE) {
                val pixelValue = pixelValues[pixel++]
                byteBuffer.putFloat((pixelValue shr 16 and 0xFF) / 255f)
                byteBuffer.putFloat((pixelValue shr 8 and 0xFF) / 255f)
                byteBuffer.putFloat((pixelValue and 0xFF) / 255f)
            }
        }

        model.run(byteBuffer, result)
        return returnResults(result)
    }
    /*Function which take 3 best recognition and joined them with propabilities*/
    private fun returnResults(result: Array<FloatArray>): List<Recognition> {

        val recognitions = mutableListOf<Recognition>()

        labels.forEachIndexed { index, label ->
                val probability = result[0][index]
                recognitions.add(Recognition(label, probability))
            }


        return recognitions.sortedByDescending{ it.probability }.take(3)
    }
    /*Made by using TensorFlow site*/
    @Throws(IOException::class)
    private fun getModel(assetManager: AssetManager, modelPath: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            .asReadOnlyBuffer()
    }
    /*Saving Labels in list from txt*/
    @Throws(IOException::class)
    private fun getLabels(assetManager: AssetManager, labelPath: String): List<String> {
        val labels = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(assetManager.open(labelPath)))
        while (true) {
            val label = reader.readLine() ?: break
            labels.add(label)
        }
        reader.close()
        return labels
    }
    /*Useful const*/
    companion object {
        private const val BATCH_SIZE = 1 // process only 1 image at a time
        private const val MODEL_INPUT_SIZE = 224 // 224x224
        private const val BYTES_PER_CHANNEL = 4 // float size
        private const val PIXEL_SIZE = 3 // rgb

        private const val LABELS_PATH = "labels.txt"
        private const val MODEL_PATH = "model.tflite"
    }

}