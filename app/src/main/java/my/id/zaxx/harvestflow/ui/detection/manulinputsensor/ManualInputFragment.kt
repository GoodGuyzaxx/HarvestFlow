package my.id.zaxx.harvestflow.ui.detection.manulinputsensor


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.id.zaxx.harvestflow.databinding.FragmentManualInputBinding
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity
import my.id.zaxx.harvestflow.utils.PrediksiKualitas

class ManualInputFragment : Fragment() {
    private var _binding : FragmentManualInputBinding? = null
    private val binding get() = _binding!!

    private lateinit var prediksiKualitas : PrediksiKualitas


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
        prediksiKualitas = PrediksiKualitas(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPrediksi.setOnClickListener {
            runPrediciton()
        }
    }


    private fun runPrediciton() {
        val suhu = binding.textEditSuhu.text.toString().toFloat()
        val kelembabanUdara = binding.textEditKelembabanUdara.text.toString().toFloat()
        val kelembabanTanah = binding.textEditKelembabanTanah.text.toString().toFloat()
        val intensitasCahaya = binding.textEditCahaya.text.toString().toFloat()
        val inputValue = floatArrayOf(
            suhu,
            kelembabanUdara,
            kelembabanTanah,
            intensitasCahaya
        )


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = prediksiKualitas.predict(inputValue)
                activity?.runOnUiThread {
//                    Toast.makeText(requireContext(), "Sucess $result", Toast.LENGTH_LONG).show()
                    val i = Intent(requireActivity(), ResultActivity::class.java)
                    i.putExtra("HASIL",result)
                    startActivity(i)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Fail $e", Toast.LENGTH_LONG).show()
                    Log.e("TAG", "ErrorrunPrediciton: ${e}", )
                }
            }

        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}