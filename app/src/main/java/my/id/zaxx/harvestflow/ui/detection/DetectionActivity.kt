package my.id.zaxx.harvestflow.ui.detection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import my.id.zaxx.harvestflow.adapter.DetectionPagesAdapter
import my.id.zaxx.harvestflow.databinding.ActivityDetectionBinding
import my.id.zaxx.harvestflow.ui.detection.manulinputsensor.ManualInputFragment
import my.id.zaxx.harvestflow.ui.detection.sensorinput.SensorInputFragment

class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding
    private lateinit var viewPagerAdapter : DetectionPagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()

    }

    private fun setupViewPager(){
        val listOfFragment = listOf(
            SensorInputFragment(),
            ManualInputFragment()
        )

        viewPagerAdapter = DetectionPagesAdapter(
            listOfFragment,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when ( position) {
                0 -> "Sensor Input"
                1 -> "Manual Input"
                else -> ""
            }
        }.attach()
    }
}