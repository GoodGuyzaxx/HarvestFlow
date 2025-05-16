package my.id.zaxx.harvestflow.ui.detection.sensorinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.id.zaxx.harvestflow.databinding.FragmentSensorInputBinding


class SensorInputFragment : Fragment() {

    private var _binding : FragmentSensorInputBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSensorInputBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    companion object {

    }
}