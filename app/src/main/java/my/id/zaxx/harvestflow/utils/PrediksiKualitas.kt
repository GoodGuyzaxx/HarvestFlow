package my.id.zaxx.harvestflow.utils

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.nio.FloatBuffer


class PrediksiKualitas(context: Context) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession
    private val inputShape = longArrayOf(1, 4)
    private var predictonResult : String =""
//    private val featureNames = listOf("suhu_udara", "kelembaban_udara", "kelembaban_tanah", "intensitas_cahaya")
//    private val classNames = listOf("Baik", "Buruk", "Sedang")

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

            val outputTensor = result.get(0).value as Array<String>
            val predictedClass = outputTensor[0]

            inputTensor.close()
            result.close()
            predictonResult = predictedClass
        }catch (e : Exception){

        }
        return predictonResult
    }
}