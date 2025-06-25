package my.id.zaxx.harvestflow.ui.detection.manulinputsensor


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.id.zaxx.harvestflow.databinding.FragmentManualInputBinding
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity
import my.id.zaxx.harvestflow.utils.CheckInternet
import my.id.zaxx.harvestflow.utils.PrediksiKualitas

@AndroidEntryPoint
class ManualInputFragment : Fragment() {
    private var _binding : FragmentManualInputBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ManualInputViewModel by viewModels()

    private lateinit var prediksiKualitas : PrediksiKualitas
    private lateinit var checkInternet : CheckInternet


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
        checkInternet = CheckInternet()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPrediksi.setOnClickListener {
            if (formValidation()){
                internetStatus()
            }
        }

    }

    fun internetStatus(){
        val status = checkInternet.isInternetAvailable(requireActivity())
        if (status == true ){
            getOnlinePrediction()
            viewModel.responseValue.observe(viewLifecycleOwner){
                val baik = it.probabilities.baik.toString()
                val buruk = it.probabilities.buruk.toString()
                if (it.status == "success"){
                    val i = Intent(requireActivity(), ResultActivity::class.java)
                    i.putExtra(PREDICITON, it.prediction)
                    i.putExtra(PREDICITON_BAIK,baik)
                    i.putExtra(PREDICITON_BURUK,buruk)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    requireActivity().finish()
                }else {
                    Toast.makeText(requireContext(), "Terjadi Masalah Sialhkan Coba Lagi Nanti", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            runPrediciton()
        }
    }

    private fun getOnlinePrediction(){
        val suhu = binding.textEditSuhu.text.toString()
        val kelembabanUdara = binding.textEditKelembabanUdara.text.toString()
        val kelembabanTanah = binding.textEditKelembabanTanah.text.toString()
        val intensitasCahaya = binding.textEditCahaya.text.toString()
        val jsonObject = JsonObject().apply {
            addProperty("suhu_udara", suhu)
            addProperty("kelembaban_udara", kelembabanUdara)
            addProperty("kelembaban_tanah", kelembabanTanah)
            addProperty("intensitas_cahaya", intensitasCahaya)
        }
        viewModel.getPrediciton(jsonObject)

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

    private fun formValidation(): Boolean {
        val suhu = binding.textEditSuhu.text.toString()
        val kelembabanUdara = binding.textEditKelembabanUdara.text.toString()
        val kelembabanTanah = binding.textEditKelembabanTanah.text.toString()
        val intensitasCahaya = binding.textEditCahaya.text.toString()

        return when {
            suhu.isEmpty() -> {
                binding.textEditSuhu.error = "Tidak Boleh Kosong"
                false
            }
            kelembabanUdara.isEmpty() -> {
                binding.textEditKelembabanUdara.error = "Tidak Boleh Kosong"
                false
            }
            kelembabanTanah.isEmpty() -> {
                binding.textEditKelembabanTanah.error = "Tidak Boleh Kosong"
                false
            }
            intensitasCahaya.isEmpty() -> {
                binding.textEditCahaya.error = "Tidak Boleh Kosong"
                false
            }
            else -> true
        }
    }

    companion object {
        private const val PREDICITON = "prediksi"
        private const val PREDICITON_BAIK = "baik"
        private const val PREDICITON_BURUK = "buruk"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}