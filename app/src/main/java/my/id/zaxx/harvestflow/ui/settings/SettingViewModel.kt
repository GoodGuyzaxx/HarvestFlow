package my.id.zaxx.harvestflow.ui.settings

import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.id.zaxx.harvestflow.data.pref.SystemPreferences
import my.id.zaxx.harvestflow.data.pref.ThemeModel
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(private val pref: SystemPreferences) : ViewModel() {

    fun saveTheme(theme : Boolean){
        viewModelScope.launch {
            pref.saveTheme(ThemeModel(
                isDarkMode = theme
            ))
        }
    }

    fun getTheme() : LiveData<ThemeModel>{
        return pref.getTheme().asLiveData()
    }
}