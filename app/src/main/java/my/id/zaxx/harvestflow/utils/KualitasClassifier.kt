package my.id.zaxx.harvestflow.utils

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.schema.Model

import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class KualitasClassifier(private val context: Context ) {
    private var interpreter: Interpreter? =null
    private val NUM_CLASS = 3

    val clases = arrayOf("Buruk","Sedang","Baik")

    init {
        try {
            val model = loadModelFile()
            val options = Interpreter.Options()
            options.setNumThreads(2)
            interpreter = Interpreter(model, options)
        }catch (e : Exception){
            Log.d("TAG", "Model Error $e")
        }
    }

    private fun loadModelFile(): ByteBuffer {
        val assetManager = context.assets
        val modelFile = assetManager.openFd("model.tflite")
        val inputStream = FileInputStream(modelFile.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = modelFile.startOffset
        val declaredLength = modelFile.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(suhuUdara: Float, kelembabanUdara: Float, kelembabanTanah: Float, intensitasCahaya: Float): FloatArray {
        // Siapkan input buffer
        val inputBuffer = ByteBuffer.allocateDirect(4 * 4) // 4 features * 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder())

        // Masukkan data
        inputBuffer.putFloat(suhuUdara)
        inputBuffer.putFloat(kelembabanUdara)
        inputBuffer.putFloat(kelembabanTanah)
        inputBuffer.putFloat(intensitasCahaya)
        inputBuffer.rewind()

        // Siapkan output buffer
        // Asumsikan 3 kelas (sesuaikan dengan model Anda)
        val outputBuffer = Array(1) { FloatArray(3) }

        // Jalankan inferensi
        interpreter?.run(inputBuffer, outputBuffer)
        val result = outputBuffer[0]
        var maxIndex = 0
        for (i in 1 until NUM_CLASS ){
            if (result[i] > result[maxIndex]){
                maxIndex = i
            }
        }

        return outputBuffer[0]

//        return KualitasResult(
//            clases[maxIndex],
//            result[maxIndex],
//            result,
//        )

        Log.d("TAG", "predict: $outputBuffer[0]")
    }

    fun close() {
        interpreter?.close()
    }

    data class KualitasResult(
        val kategori: String,          // Label kategori kualitas hasil prediksi
        val confidence: Float,         // Confidence score untuk kategori tersebut
        val allScores: FloatArray,     // Semua confidence scores
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as KualitasResult

            if (kategori != other.kategori) return false
            if (confidence != other.confidence) return false
            if (!allScores.contentEquals(other.allScores)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = kategori.hashCode()
            result = 31 * result + confidence.hashCode()
            result = 31 * result + allScores.contentHashCode()
            return result
        }
    }


}
