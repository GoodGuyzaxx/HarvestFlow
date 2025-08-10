package my.id.zaxx.harvestflow.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import my.id.zaxx.harvestflow.data.pref.SystemPreferences
import my.id.zaxx.harvestflow.data.pref.ThemeModel
import java.io.PipedReader
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val pref: SystemPreferences): ViewModel(){

    fun getTheme(): LiveData<ThemeModel>{
        return pref.getTheme().asLiveData()
    }
}