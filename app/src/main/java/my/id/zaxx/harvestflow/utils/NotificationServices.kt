package my.id.zaxx.harvestflow.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.data.model.SoilSensor
import my.id.zaxx.harvestflow.data.model.TempSensor
import my.id.zaxx.harvestflow.ui.home.HomeFragment
import my.id.zaxx.harvestflow.ui.home.HomeFragment.Companion.CHANNEL_ID

class NotificationServices: Service() {
    private lateinit var firebase: FirebaseDatabase
    private lateinit var database: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
//                startForegroundServiceWithNotification()
                start()  // start your firebase listener & logic
            }

            Actions.STOP.toString() -> stopSelf()
        }
        return START_NOT_STICKY
    }

    enum class Actions {
        START, STOP
    }

    private fun start() {
        getTempSensor()
        getSoilSensor()
    }

    private fun getSoilSensor() {
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("soil_sensor")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val valueSoil = snapshot.getValue(SoilSensor::class.java)
                valueSoil?.let {
                    if (it.value > 80){
                         sendNotification(resources.getString(R.string.plant_emoji)+" Kelembaban tanah melebihi 80%. Akar tanaman berisiko membusuk dan pertumbuhan tanaman dapat terganggu akibat kelebihan air.")
                    } else if (it.value <= 60)
                        sendNotification(resources.getString(R.string.plant_emoji)+" Kelembaban tanah di bawah 60%. Tanaman berpotensi mengalami kekeringan, daun menggulung, dan buah kecil.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: $error",)
            }
        }
        database.addValueEventListener(valueEventListener!!)
    }

//    private fun getLuxSenor(){
//        firebase = FirebaseDatabase.getInstance()
//        database = firebase.getReference("light_sensor")
//
//        valueEventListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val luxValue = snapshot.getValue(LightSensor::class.java)
//                luxValue?.let {
//                    if (it.lux_value > 50000) {
//                        sendNotification("Kontol", resources.getString(R.string.lorem_ipsum))
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "onCancelled: $error")
//            }
//        }
//        database.addValueEventListener(valueEventListener!!)
//    }

    private fun getTempSensor() {
        firebase = FirebaseDatabase.getInstance()
        database = firebase.getReference("temp_sensor")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempSensor = snapshot.getValue(TempSensor::class.java)
                tempSensor?.let {
                    if (it.celcius < 25){
                        sendNotification(resources.getString(R.string.cold_emoji)+" Suhu udara saat ini di bawah 25°C. Kondisi ini dapat memperlambat proses fotosintesis dan meningkatkan risiko serangan jamur pada tanaman cabai rawit")
                    } else if (it.celcius > 31) {
                        sendNotification(resources.getString(R.string.temp_emoji)+" Suhu udara saat ini melebihi 31°C. Kondisi ini dapat menyebabkan tanaman mengalami stres panas, daun layu, dan bunga mudah rontok")
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        // Attach the listener
        database.addValueEventListener(valueEventListener!!)
    }


    @SuppressLint("MissingPermission")
    fun sendNotification(textContent : String){
        val intent = Intent(applicationContext, HomeFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,0,intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "background_channel"
        val textTitle = resources.getString(R.string.warning_emoji) + " Peringatan"

        // Create notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Background Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for background notifications"
            }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_microchip)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(textContent))
            .build()

        startForeground(2,builder)
    }

//    private fun startForegroundServiceWithNotification() {
//        val channelId = "foreground_service_channel"
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(~
//                channelId,
//                "Foreground Service Channel",
//                NotificationManager.IMPORTANCE_LOW
//            ).apply {
//                description = "Foreground Service running"
//            }
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Monitoring Sensor")
//            .setContentText("Sensor Monitoring Akitf")
//            .setSmallIcon(R.drawable.ic_microchip)
//            .setOngoing(true)
//            .build()
//
//        // Promote service to foreground
//        startForeground(2, notification)
//    }




    override fun onDestroy() {
        super.onDestroy()
    }
}