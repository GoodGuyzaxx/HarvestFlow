package my.id.zaxx.harvestflow.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.data.model.LightSensor
import my.id.zaxx.harvestflow.data.model.SoilSensor
import my.id.zaxx.harvestflow.data.model.TempSensor
import my.id.zaxx.harvestflow.databinding.FragmentHomeBinding
import my.id.zaxx.harvestflow.utils.NotificationServices
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
    private var valueEventListener: ValueEventListener? = null
    private val viewModel : HomeViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container ,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        /*Function To Run*/
        getNotifChannel()
        requestAllPermissions()
        getTempSensor()
        getSoilSensor()
        getLuxSensor()
        getWeatherStatus()


        binding.tvDateTime.text = formatDate()
        viewModel.errorResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it , Toast.LENGTH_SHORT).show()
        }

        viewModel.weatherResponse.observe(viewLifecycleOwner){
            val valueCurrentTemp = convertKelvinToCelcius(it.main.temp.toString().toDouble())
            binding.tvCurrentTemp.text = resources.getString(R.string.room_temp, valueCurrentTemp)
            binding.tvLocation.text = it.name.toString()
            binding.tvWindSpeed.text = resources.getString(R.string.wind_speed, it.wind.speed)
            binding.tvWeatherDescription.text = it.weather[0].description.toString()

        }
    }

    /*Get Sensor Value From Database*/
    private fun getTempSensor() {
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("temp_sensor")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val tempSensor = snapshot.getValue(TempSensor::class.java)
                tempSensor?.let {
                    val valueCel = it.celcius.toString()
                    val valueHumd = it.humd.toString()
                    binding.tvTemperature.text = resources.getString(R.string.room_temp,valueCel )
                    binding.tvEnvHumidity.text = resources.getString(R.string.env_humi, valueHumd)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the listener
        database.addValueEventListener(valueEventListener!!)
    }

    /*Get Sensor Value From Database*/
    private fun getSoilSensor(){
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("soil_sensor")

        valueEventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val valueSoil = snapshot.getValue(SoilSensor::class.java)
                valueSoil?.let {
                    binding.tvHumidity.text = resources.getString(R.string.soil_humi, it.value.toString())
                    binding.progressSoilHumidity.setProgress(it.value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        database.addValueEventListener(valueEventListener!!)
    }

    /*Get Sensor Value From Database*/
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

    /*To Make Notification Channel For Android 8 and Higher*/
    private fun getNotifChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /*Request For ASK 2 Permission*/
    private fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                100
            )
        }
    }

    /*Get Longitude and Latitude */
    @SuppressLint("MissingPermission")
    private fun getWeatherStatus(){
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it!=null){
                val textLat = it.latitude.toString()
                val textLon = it.longitude.toString()
                Log.d("TAG", "getWeatherStatus: $textLon + $textLat")
                viewModel.getWeather(textLat,textLon)

            }
        }
    }

    /*For Convert Kelvin To Celcius In Api Weather*/
    private fun convertKelvinToCelcius(value : Double): Any {
        val kMinus = -273.15
        val cValue = value + kMinus
        return  cValue.roundToInt()
    }

    /*For Change Format Date*/
    private fun formatDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }


    override fun onDestroyView() {
        // Remove the Firebase listener
        valueEventListener?.let { listener ->
            database.removeEventListener(listener)
            valueEventListener = null
        }


        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val CHANNEL_ID = "zaxx"
        const val NOTIFICATION_ID = 0
        const val DESCRIPTION = "NOTIF CHANNEL"
    }

}