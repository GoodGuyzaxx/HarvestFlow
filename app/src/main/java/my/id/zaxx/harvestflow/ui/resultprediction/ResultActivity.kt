package my.id.zaxx.harvestflow.ui.resultprediction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import my.id.zaxx.harvestflow.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentValue = intent.extras?.getString("HASIL",null)
        binding.tvCondition.text = intentValue
    }



}