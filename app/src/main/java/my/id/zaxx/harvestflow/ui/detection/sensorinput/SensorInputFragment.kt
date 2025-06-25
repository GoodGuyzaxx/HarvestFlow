package my.id.zaxx.harvestflow.ui.detection.sensorinput

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.data.model.LightSensor
import my.id.zaxx.harvestflow.data.model.SoilSensor
import my.id.zaxx.harvestflow.data.model.TempSensor
import my.id.zaxx.harvestflow.databinding.FragmentSensorInputBinding
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity


@AndroidEntryPoint
class SensorInputFragment : Fragment() {
    private var _binding: FragmentSensorInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebase: FirebaseDatabase
    private val viewModel: SensorInputViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSensorInputBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnPredict.setOnClickListener {
            getSensorValue()
        }

        viewModel.responsePrediction.observe(viewLifecycleOwner) {
            val baik = it.probabilities.baik.toString()
            val buruk = it.probabilities.buruk.toString()
            if (it.status == "success") {
                val i = Intent(requireContext(), ResultActivity::class.java)
                i.putExtra(PREDICITON, it.prediction)
                i.putExtra(PREDICITON_BAIK, baik)
                i.putExtra(PREDICITON_BURUK, buruk)
                startActivity(i)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Terjadi Masalah Silakan Coba Lagi Nanti", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun getSensorValue() {
        firebase = FirebaseDatabase.getInstance()


        var lightValue: Int? = null
        var soilValue: Int? = null
        var tempValue: Int? = null
        var humidityValue: Int? = null


        var completedReadings = 0
        val totalReadings = 3


        fun checkAndMakePrediction() {
            if (completedReadings == totalReadings &&
                lightValue != null && soilValue != null &&
                tempValue != null && humidityValue != null) {

                val jsonObject = JsonObject().apply {
                    addProperty("suhu_udara", tempValue)
                    addProperty("kelembaban_udara", humidityValue)
                    addProperty("kelembaban_tanah", soilValue)
                    addProperty("intensitas_cahaya", lightValue)
                }

                viewModel.getPrediciton(jsonObject)
            }
        }

        /* READ LIGHT SENSOR */
        val lightDatabase = firebase.getReference("light_sensor")
        val lightListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val luxSensor = snapshot.getValue(LightSensor::class.java)
                luxSensor?.let {
                    lightValue = it.lux_value
                    Log.d(TAG, "Light sensor: $lightValue")
                    completedReadings++
                    checkAndMakePrediction()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    activity,
                    "Failed to retrieve light data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        lightDatabase.addValueEventListener(lightListener)

        /* READ SOIL SENSOR */
        val soilDatabase = firebase.getReference("soil_sensor")
        val soilListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val soilSensor = snapshot.getValue(SoilSensor::class.java)
                soilSensor?.let {
                    soilValue = it.value
                    completedReadings++
                    checkAndMakePrediction()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    activity,
                    "Failed to retrieve soil data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        soilDatabase.addValueEventListener(soilListener)

        /* READ DHT SENSOR */
        val tempDatabase = firebase.getReference("temp_sensor")
        val tempListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val tempSensor = snapshot.getValue(TempSensor::class.java)
                tempSensor?.let {
                    tempValue = it.celcius
                    humidityValue = it.humd
                    completedReadings++
                    checkAndMakePrediction()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    activity,
                    "Failed to retrieve temperature data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        tempDatabase.addValueEventListener(tempListener)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val PREDICITON = "prediksi"
        private const val PREDICITON_BAIK = "baik"
        private const val PREDICITON_BURUK = "buruk"
        private val TAG = SensorInputFragment::class.simpleName
    }

}