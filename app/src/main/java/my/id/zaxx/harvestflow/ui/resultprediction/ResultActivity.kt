package my.id.zaxx.harvestflow.ui.resultprediction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentValue = intent.getStringExtra(PREDICITON)

        binding.tvCondition.text = intentValue.toString()

        val baik = intent.getStringExtra(PREDICITON_BAIK)
        val convertBaik = baik?.toFloat() ?: 0F
        val buruk = intent.getStringExtra(PREDICITON_BURUK)
        val converBuruk = buruk?.toFloat() ?: 0F

//        binding.barChart.visibility = View.GONE
        val listValue = arrayListOf<Float>(
            convertBaik * 100,
            converBuruk * 100)

        binding.tvAccuracy.text = resources.getString(R.string.confidence,listValue.max().toInt().toString())
        setupBarChart(listValue)
    }


    private fun setupBarChart(values : ArrayList<Float>) {
        val conditions = listOf("Baik", "Buruk")

            // Membuat entries untuk chart
            val entries = ArrayList<BarEntry>().apply {
                for (i in values.indices) {
                    add(BarEntry(i.toFloat(), values[i]))
                }
            }


            // Membuat dataset
            val dataSet = BarDataSet(entries,"Prediksi").apply {
                setColors(
                    resources.getColor(R.color.md_theme_inversePrimary),
                    resources.getColor(R.color.md_theme_error_mediumContrast),
                )
                valueTextColor = resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                valueTextSize = 14f
            }

            // Konfigurasi chart
            with(binding.barChart) {
                data = BarData(dataSet)

                // Konfigurasi sumbu X
                xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return conditions.getOrNull(value.toInt()) ?: ""
                        }
                    }
                    axisLineColor = resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                    gridColor =resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                    textColor =resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                }

                // Konfigurasi sumbu Y
                axisLeft.apply {
                    axisMinimum = 0f
                    granularity = 20f
                    textColor = resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false

                // Konfigurasi legend
                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    textColor = resources.getColor(R.color.md_theme_secondaryContainer_highContrast)
                    setDrawInside(false)
                }

                // Animasi
                animateY(1000)
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)

        }

    }

    companion object {
        private const val PREDICITON = "prediksi"
        private const val PREDICITON_BAIK = "baik"
        private const val PREDICITON_BURUK = "buruk"
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}