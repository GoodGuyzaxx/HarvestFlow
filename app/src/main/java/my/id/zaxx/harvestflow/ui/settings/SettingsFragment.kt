package my.id.zaxx.harvestflow.ui.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import my.id.zaxx.harvestflow.BuildConfig
import my.id.zaxx.harvestflow.R
import my.id.zaxx.harvestflow.databinding.FragmentSettingsBinding
import my.id.zaxx.harvestflow.ui.resultprediction.ResultActivity


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var switchTheme : SwitchMaterial
    private lateinit var tvVersion : TextView

    private val viewModel: SettingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchTheme = view.findViewById(R.id.theme_switch)
        tvVersion = view.findViewById<TextView>(R.id.tv_version_name)

        viewModel.getTheme().observe(viewLifecycleOwner){
            if (!it.isDarkMode) {
                switchTheme.isChecked = false
            } else {
                switchTheme.isChecked = true
            }
        }

        switchTheme.setOnClickListener {
            if (switchTheme.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                viewModel.saveTheme(true)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                viewModel.saveTheme(false)
            }
        }

        tvVersion.text = BuildConfig.VERSION_NAME.toString()


        binding.cardClearData.setOnClickListener {
            val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            i.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))
            startActivity(i)

        }

        binding.btnExit.setOnClickListener {
            requireActivity().finish()
        }



    }


}