package my.id.zaxx.harvestflow.utils

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.nio.FloatBuffer
import java.util.Collections

// Class untuk menyimpan hasil prediksi beserta probabilitasnya
data class HasilPrediksi(
    val kelasHasil: String,                 // Kelas hasil prediksi ("Baik", "Buruk", "Sedang")
    val probabilitas: Map<String, Float>,   // Map dari nama kelas ke nilai probabilitas
    val tertinggi: Float                    // Nilai probabilitas tertinggi
)

class PrediksiKualitas(context: Context) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession
    private val inputShape = longArrayOf(1, 4)
    private val featureNames = listOf("suhu_udara", "kelembaban_udara", "kelembaban_tanah", "intensitas_cahaya")
    private val classNames = listOf("Baik", "Buruk", "Sedang")

    init {
        val modelFile = context.assets.open("custom_model.onnx")
        ortSession = ortEnv.createSession(modelFile.readBytes())
        println("Input names: ${ortSession.inputNames}")
        println("Output names: ${ortSession.outputNames}")
    }

    // Fungsi prediksi yang mengembalikan HasilPrediksi dengan nilai probabilitas
    fun predict(inputValues: FloatArray): HasilPrediksi {
        require(inputValues.size == 4) { "Input harus memiliki 4 nilai" }

        // Membuat input tensor
        val inputBuffer = FloatBuffer.wrap(inputValues)
        val inputTensor = OnnxTensor.createTensor(ortEnv, inputBuffer, inputShape)

        // Menjalankan inference dengan nama input yang benar
        val inputName = ortSession.inputNames.first()
        val results = ortSession.run(Collections.singletonMap(inputName, inputTensor))

        // Mengambil output
        val outputName = ortSession.outputNames.first()
        val outputValue = results[outputName]?.get()?.value

        val outputInfo = ortSession.outputInfo

        return when {
            outputValue is OnnxTensor -> {
                // Handle tensor output (probabilities)
                val outputBuffer = outputValue.floatBuffer
                val probabilities = FloatArray(classNames.size)
                outputBuffer.get(probabilities)

                // Membuat map kelas ke probabilitas
                val probabilitasMap = classNames.mapIndexed { index, className ->
                    className to probabilities[index]
                }.toMap()

                // Mendapatkan index kelas dengan probabilitas tertinggi
                val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                val kelasTerpilih = if (maxIndex != -1) classNames[maxIndex] else "Tidak diketahui"
                val nilaiTertinggi = if (maxIndex != -1) probabilities[maxIndex] else 0f

                HasilPrediksi(kelasTerpilih, probabilitasMap, nilaiTertinggi)
            }

            outputValue is Array<*> && outputValue.isNotEmpty() -> {
                // Handle string array output (direct class names)
                // Dalam kasus ini, kita tidak memiliki nilai probabilitas
                // Jadi kita memberikan nilai 1.0f untuk kelas yang diprediksi
                val predictedClass = outputValue[0] as String
                val probabilitasMap = classNames.associateWith { if (it == predictedClass) 1.0f else 0.0f }

                HasilPrediksi(predictedClass, probabilitasMap, 1.0f)
            }

            else -> {
                throw IllegalStateException("Tipe output tidak dikenali")
            }
        }
    }

    // Versi lama predict untuk backward compatibility
    fun predictClass(inputValues: FloatArray): String {
        return predict(inputValues).kelasHasil
    }

    // Mendapatkan nilai probabilitas untuk setiap kelas
    fun getProbabilities(inputValues: FloatArray): Map<String, Float> {
        return predict(inputValues).probabilitas
    }

    fun getFeatureNames(): List<String> = featureNames

    fun getClassNames(): List<String> = classNames
}