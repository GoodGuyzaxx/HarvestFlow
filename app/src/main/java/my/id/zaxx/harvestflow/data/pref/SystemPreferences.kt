package my.id.zaxx.harvestflow.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")
class SystemPreferences @Inject constructor(private val pref : DataStore<Preferences>) {

    suspend fun saveTheme(theme : ThemeModel){
        pref.edit { data ->
            data[THEME_KEY] = theme.isDarkMode
        }
    }

    fun getTheme(): Flow<ThemeModel>{
        return pref.data.map { data ->
            ThemeModel(
                data[THEME_KEY] ?: true
            )
        }
    }


    companion object {
        private val THEME_KEY = booleanPreferencesKey("theme")
    }
}