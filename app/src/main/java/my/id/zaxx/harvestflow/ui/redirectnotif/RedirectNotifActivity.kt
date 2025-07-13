package my.id.zaxx.harvestflow.ui.redirectnotif

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.ActivityRedirectNotifBinding
import my.id.zaxx.harvestflow.ui.MainActivity
import my.id.zaxx.harvestflow.ui.home.HomeFragment
import my.id.zaxx.harvestflow.utils.NotificationServices

class RedirectNotifActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRedirectNotifBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedirectNotifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val valueRedirect = intent.getStringExtra("value")

        //Trigger Notification Services
        Intent(this, NotificationServices::class.java).also{
            it.action = NotificationServices.Actions.START.toString()
            this.startService(it)
        }

        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }
}