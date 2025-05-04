package my.id.zaxx.harvestflow.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity

class SettingsFragment : Fragment() {
    private lateinit var btnExit : Button
    private lateinit var switchTheme : SwitchMaterial
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnExit = view.findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            val v = Intent(context, ResultActivity::class.java)
            startActivity(v)
        }
        switchTheme = view.findViewById(R.id.theme_switch)

        // Saving state of our app using SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", true)

        if (isDarkModeOn!!) {
            switchTheme.isChecked = true
        } else {
            switchTheme.isChecked = false
        }

        switchTheme.setOnClickListener {
            if (switchTheme.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor?.putBoolean("IsDarkModeOn", false)
                editor?.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor?.putBoolean("IsDarkModeOn", true)
                editor?.apply()
            }
        }
    }
}