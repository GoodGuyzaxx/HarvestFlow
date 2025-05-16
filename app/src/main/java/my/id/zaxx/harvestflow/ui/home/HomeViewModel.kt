package my.id.zaxx.harvestflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.id.zaxx.harvestflow.data.api.response.WeatherResponse
import my.id.zaxx.harvestflow.data.repository.HarvestFlowRepository
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor (private val repository: HarvestFlowRepository):ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> = _weatherResponse

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse: LiveData<String> = _errorResponse

    fun getWeather(lat: String, lon:String){
        viewModelScope.launch {
            try {
                val response = repository.getWeather(lat,lon)
                _weatherResponse.postValue(response)
            }catch (e : HttpException){
                _errorResponse.postValue(e.toString())
            }catch (e : Exception){
                _errorResponse.postValue(e.toString())
            }

        }
    }
}