package my.id.zaxx.harvestflow.ui.detection.manulinputsensor


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.FragmentManualInputBinding
import my.id.zaxx.harvestflow.utils.KualitasClassifier

class ManualInputFragment : Fragment() {
    private var _binding : FragmentManualInputBinding? = null
    private val binding get() = _binding!!

    private lateinit var prediksi : KualitasClassifier

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManualInputBinding.inflate(layoutInflater,container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prediksi = KualitasClassifier(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPrediksi.setOnClickListener {
            getPrediksi()
        }
    }


    fun getPrediksi(){
        try {
            val suhu = binding.textEditSuhu.text.toString().toFloat()
            val kelembabanUdara = binding.textEditKelembabanUdara.text.toString().toFloat()
            val kelembabanTanah = binding.textEditKelembabanTanah.text.toString().toFloat()
            val intensitasCahaya = binding.textEditCahaya.text.toString().toFloat()

            val result = prediksi.predict(suhu,kelembabanUdara,kelembabanTanah,intensitasCahaya)

            Log.d("TAG", "getPrediksi: ${result} ")
        }catch (e: Exception){
            Log.d("TAG", "getPrediksiError: $e")
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        prediksi.close()
    }

}