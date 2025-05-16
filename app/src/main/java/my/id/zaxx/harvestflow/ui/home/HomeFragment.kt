package my.id.zaxx.harvestflow.ui.home

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.data.model.LightSensor
import my.id.zaxx.harvestflow.data.model.RainSensor
import my.id.zaxx.harvestflow.data.model.RelayPower
import my.id.zaxx.harvestflow.data.model.SoilSensor
import my.id.zaxx.harvestflow.data.model.TempSensor
import my.id.zaxx.harvestflow.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebase : FirebaseDatabase
    private lateinit var database: DatabaseReference
    private lateinit var power : RelayPower
    private var readPowerValue : Boolean = false
    private var valueEventListener: ValueEventListener? = null
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container ,false)
//        buttonTheme()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTempSensor()
        getSoilSensor()
        getRainSensor()
        getLuxSensor()
//        getRelay()
//        binding.btnPump.setOnClickListener {
//            buttonTheme()
//            if (readPowerValue == true){
//                sendRelay(false)
//            } else {
//                sendRelay(true)
//            }
//        }


        val lat = "-2.53371"
        val lon = "140.71813"

        binding.tvDateTime.text = formatDate()

        viewModel.errorResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it , Toast.LENGTH_SHORT).show()
        }

        viewModel.getWeather(lat,lon)
        viewModel.weatherResponse.observe(viewLifecycleOwner){
            val valueCurrentTemp = convertKelvinToCelcius(it.main.temp.toString().toDouble())


            binding.tvCurrentTemp.text = resources.getString(R.string.room_temp, valueCurrentTemp)
            binding.tvLocation.text = it.name.toString()
            binding.tvWindSpeed.text = resources.getString(R.string.wind_speed, it.wind.speed)
            binding.tvWeatherDescription.text = it.weather[0].description.toString()

        }
    }


    private fun getTempSensor() {
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("temp_sensor")

        // Create the listener
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val tempSensor = snapshot.getValue(TempSensor::class.java)
                tempSensor?.let {
                    binding.tvTemperature.text = resources.getString(R.string.room_temp,it.celcius )
                    binding.tvEnvHumidity.text = resources.getString(R.string.env_humi, it.humd)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the listener
        database.addValueEventListener(valueEventListener!!)
    }

    private fun getSoilSensor(){
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("soil_sensor")

        valueEventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val valueSoil = snapshot.getValue(SoilSensor::class.java)
                valueSoil?.let {
                    binding.tvHumidity.text = resources.getString(R.string.soil_humi, it.value)
                    binding.progressSoilHumidity.setProgress(it.value.toInt())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        database.addValueEventListener(valueEventListener!!)
    }

    private fun getRainSensor(){
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("water_sensor")

        valueEventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val rainSensorValue = snapshot.getValue(RainSensor::class.java)
                rainSensorValue?.let {
                    binding.tvRainStatus.text = it.status
                    Log.d("TAG", "onDataChangeRain: ${it.status}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(valueEventListener!!)
    }

    private fun getLuxSensor(){
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("light_sensor")

        valueEventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val luxValue = snapshot.getValue(LightSensor::class.java)
                luxValue?.let {
                    binding.tvLight.text = resources.getString(R.string.lux_sensor , it.lux_value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(valueEventListener!!)
    }

    private fun convertKelvinToCelcius(value : Double): Any {
        val kMinus = -273.15
        val cValue = value + kMinus
        return  cValue.roundToInt()
    }

    private fun formatDate(): String {
        // Create formatter with the desired pattern
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

        // You can use current date or a specific date
        val currentDate = Date() // For current date

        // For a specific date (e.g., March 22, 2025)
        // val specificDate = Calendar.getInstance()
        // specificDate.set(2025, Calendar.MARCH, 22)
        // return dateFormat.format(specificDate.time)

        return dateFormat.format(currentDate)
    }

//    private fun getRelay(){
//        firebase = FirebaseDatabase.getInstance()
//        database = firebase.getReference("relay_module")
//
//        valueEventListener = object  : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (_binding == null ) return
//                val relayPowerValue = snapshot.getValue(RelayPower::class.java)
//                relayPowerValue?.let {
//                    readPowerValue = it.power
//                    Log.d("TAG", "onDataChangeRelay: ${it.power}")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//        database.addValueEventListener(valueEventListener!!)
//    }

//    private fun sendRelay(value : Boolean){
//        firebase =FirebaseDatabase.getInstance()
//        database = firebase.getReference("relay_module")
//        database.child("power")
//        power = RelayPower()
//        power.power = value
//
//        database.setValue(power)
//            .addOnSuccessListener {
//                Toast.makeText(activity, "berhasil", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener{
//                Toast.makeText(activity, "gagal ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }


    override fun onDestroyView() {
        // Remove the Firebase listener
        valueEventListener?.let { listener ->
            database.removeEventListener(listener)
            valueEventListener = null
        }


        _binding = null
        super.onDestroyView()
    }
//
//    override fun onDestroy() {
//
//        if (::database.isInitialized && ::valueEventListener.isInitialized) {
//            database.removeEventListener(valueEventListener)
//        }
//
//        _binding = null
//
//        super.onDestroyView()
//    }
}