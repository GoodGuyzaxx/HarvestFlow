package my.id.zaxx.harvestflow.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.data.model.LightSensor
import my.id.zaxx.harvestflow.data.model.RainSensor
import my.id.zaxx.harvestflow.data.model.RelayPower
import my.id.zaxx.harvestflow.data.model.SoilSensor
import my.id.zaxx.harvestflow.data.model.TempSensor
import my.id.zaxx.harvestflow.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebase : FirebaseDatabase
    private lateinit var database: DatabaseReference
    private lateinit var power : RelayPower
    private var readPowerValue : Boolean = false
    private var valueEventListener: ValueEventListener? = null

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
        getTempSensor()
        getSoilSensor()
        getRainSensor()
        getLuxSensor()
        getRelay()
        buttnTheme()
        binding.btnPump.setOnClickListener {
            buttnTheme()
            if (readPowerValue == true){
                sendRelay(false)
            } else {
                sendRelay(true)
            }
        }
    }

    private fun buttnTheme(){
        if (readPowerValue == true){
            binding.btnPump.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background)
            binding.btnPump.text = "Matikan Pompa"
        }else{
            binding.btnPump.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_on)
            binding.btnPump.text = "Nyalakan Pompa"
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
                Log.d("TAG", "Data updated: ${tempSensor}")
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

    private fun getRelay(){
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("relay_module")

        valueEventListener = object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null ) return
                val relayPowerValue = snapshot.getValue(RelayPower::class.java)
                relayPowerValue?.let {
                    readPowerValue = it.power
                    Log.d("TAG", "onDataChangeRelay: ${it.power}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(valueEventListener!!)
    }

    private fun sendRelay(value : Boolean){
        firebase =FirebaseDatabase.getInstance()
        database = firebase.getReference("relay_module")
        database.child("power")
        power = RelayPower()
        power.power = value

        database.setValue(power)
            .addOnSuccessListener {
                Toast.makeText(activity, "berhasil", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(activity, "gagal ${it.message}", Toast.LENGTH_SHORT).show()
            }
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