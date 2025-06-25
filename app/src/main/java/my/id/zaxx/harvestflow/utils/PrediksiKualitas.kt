package my.id.zaxx.harvestflow.utils

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import java.nio.FloatBuffer


class PrediksiKualitas(context: Context) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession
    private val inputShape = longArrayOf(1, 4)
    private var predictonResult: String = ""

    // Define class names for mapping (binary classification)
    private val classNames = listOf("Baik", "Buruk")

    init {
        val modelFile = context.assets.open("custom_model.onnx")
        ortSession = ortEnv.createSession(modelFile.readBytes())
    }

    fun predict(inputValues: FloatArray): String {
        try {
            require(inputValues.size == 4) { "Input harus memiliki 4 nilai" }

            val floatBufferInput = FloatBuffer.wrap(inputValues)
            val inputTensor = OnnxTensor.createTensor(ortEnv, floatBufferInput, inputShape)

            val inputName = ortSession.inputNames.iterator().next()
            val result = ortSession.run(mapOf(inputName to inputTensor))

            // Get the output tensor - this is likely a float array or long array
            val outputTensor = result.get(0).value

            Log.d("TAG", "Output tensor type: ${outputTensor.javaClass.simpleName}")
            Log.d("TAG", "Output tensor value: $outputTensor")

            val predictedClass = when (outputTensor) {
                is LongArray -> {
                    // Model outputs class index as long array
                    val classIndex = outputTensor[0].toInt()
                    Log.d("TAG", "Predicted class index: $classIndex")
                    Log.d("TAG", "Available classes: $classNames")
                    classNames.getOrElse(classIndex) { "Unknown (index: $classIndex)" }
                }
                else -> {
                    Log.e("TAG", "Unexpected output tensor type: ${outputTensor.javaClass.simpleName}")
                    "Unknown"
                }
            }

            inputTensor.close()
            result.close()
            predictonResult = predictedClass

        } catch (e: Exception) {
            Log.e("TAG", "predictUtil: ${e.message}", e)
            predictonResult = "Error"
        }
        return predictonResult
    }

    fun close() {
        ortSession.close()
        ortEnv.close()
    }
}