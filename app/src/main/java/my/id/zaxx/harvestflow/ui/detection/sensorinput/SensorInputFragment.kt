package my.id.zaxx.harvestflow.ui.detection.sensorinput

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.databinding.FragmentSensorInputBinding
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity
import org.json.JSONObject


@AndroidEntryPoint
class SensorInputFragment : Fragment() {

    private var _binding : FragmentSensorInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SensorInputViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSensorInputBinding.inflate(inflater,container,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val suhu = 32
        val kelembabanUdara = 73
        val kelembabanTanah = 68
        val intensitasCahaya = 2600
        val jsonObject = JsonObject().apply {
            addProperty("suhu_udara", suhu)
            addProperty("kelembaban_udara", kelembabanUdara)
            addProperty("kelembaban_tanah", kelembabanTanah)
            addProperty("intensitas_cahaya", intensitasCahaya)
        }

        binding.btnPredict.setOnClickListener {
            viewModel.getPrediction(jsonObject)
        }
        viewModel.responsePrediction.observe(viewLifecycleOwner){
            val baik = it.probabilities[0].toString()
            val buruk = it.probabilities[1].toString()
            val sedang = it.probabilities[2].toString()
            if (it.status == "success") {
                val i = Intent(requireContext(), ResultActivity::class.java)
                i.putExtra(PREDICITON,it.prediction)
                i.putExtra(PREDICITON_BAIK, baik)
                i.putExtra(PREDICITON_BURUK, buruk)
                i.putExtra(PREDICITON_SEDANG, sedang)
                startActivity(i)
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val PREDICITON = "prediksi"
        private const val PREDICITON_BAIK = "baik"
        private const val PREDICITON_BURUK = "buruk"
        private const val PREDICITON_SEDANG = "sedang"
    }

}