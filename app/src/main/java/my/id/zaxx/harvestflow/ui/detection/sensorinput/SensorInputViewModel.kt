package my.id.zaxx.harvestflow.ui.detection.sensorinput

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.id.zaxx.harvestflow.data.api.response.PredictionResponse
import my.id.zaxx.harvestflow.data.repository.HarvestFlowRepository
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SensorInputViewModel @Inject constructor (private val repository: HarvestFlowRepository): ViewModel() {

    private val _responsePrediction = MutableLiveData<PredictionResponse>()
    val responsePrediction: LiveData<PredictionResponse> = _responsePrediction


    fun getPrediction(predictJson: JsonObject){
        viewModelScope.launch {
            try {
                val response = repository.getPredication(predictJson)
                _responsePrediction.postValue(response)
            }catch (e : HttpException) {
                val jsonString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonString, PredictionResponse::class.java)
                val errorMessage = errorBody
                _responsePrediction.postValue(errorBody)
                Log.d("TAG", "getLogin: $errorMessage")
            }catch (e : Exception){
                Log.d("SensorInputViewModel", "getPrediction: $e")
            }
        }
    }

}