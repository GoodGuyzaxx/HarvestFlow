package my.id.zaxx.harvestflow.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.ActivityMainBinding
import my.id.zaxx.harvestflow.ui.detection.DetectionActivity
import my.id.zaxx.harvestflow.ui.home.HomeFragment
import my.id.zaxx.harvestflow.ui.settings.SettingsFragment
import my.id.zaxx.harvestflow.utils.NotificationServices


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(HomeFragment())

        //Trigger Notification Services
        Intent(this, NotificationServices::class.java).also{
            it.action = NotificationServices.Actions.START.toString()
            this.startService(it)
        }



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