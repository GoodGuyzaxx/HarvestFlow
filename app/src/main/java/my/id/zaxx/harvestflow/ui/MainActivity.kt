package my.id.zaxx.harvestflow.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.ActivityMainBinding
import my.id.zaxx.harvestflow.ui.detection.DetectionActivity
import my.id.zaxx.harvestflow.ui.home.HomeFragment
import my.id.zaxx.harvestflow.ui.settings.SettingsFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getTheme().observe(this){data ->
            if (!data.isDarkMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        loadFragment(HomeFragment())

        binding.fab.setOnClickListener{
            val i = Intent(this@MainActivity, DetectionActivity::class.java)
            startActivity(i)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }


    }


    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
    }



}