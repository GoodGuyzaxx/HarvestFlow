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
import my.id.zaxx.harvestflow.data.api.response.Probabilities
import my.id.zaxx.harvestflow.data.repository.HarvestFlowRepository
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SensorInputViewModel @Inject constructor (private val repository: HarvestFlowRepository): ViewModel() {

    private val _responsePrediction = MutableLiveData<PredictionResponse>()
    val responsePrediction: LiveData<PredictionResponse> = _responsePrediction

    private val _errorResponse = MutableLiveData<Exception>()
    val errorResponse: LiveData<Exception> = _errorResponse

    fun getPrediciton(jsonObject: JsonObject) {
        viewModelScope.launch {
            try {
                val response = repository.getPredication(jsonObject)
                _responsePrediction.postValue(response)
            }catch (e : HttpException) {
                try {
                    val jsonString = e.response()?.errorBody()?.string()
                    if (!jsonString.isNullOrEmpty()){
                        val errorBody = Gson().fromJson(jsonString, PredictionResponse::class.java)
                        val errorMessage = errorBody
                        _responsePrediction.postValue(errorMessage)
                        Log.e("TAG", "getPrediciton:$errorMessage ")
                    } else {
                        val defaultResponse = PredictionResponse(
                            prediction = "",
                            probabilities = Probabilities("",""),
                            status = "error",
                            message = "HTTP: ${e.code()} + ${e.response()} "
                        )
                        _responsePrediction.postValue(defaultResponse)
                        Log.d(TAG, "getPrediciton: $defaultResponse")
                    }
                } catch (json : Exception) {
                    val jsonDefaultResponse = PredictionResponse(
                        prediction = "",
                        probabilities = Probabilities("",""),
                        status = "error",
                        message = "Failed ${json.message} "
                    )
                    _responsePrediction.postValue(jsonDefaultResponse)
                    Log.d(TAG, "getPrediciton: $jsonDefaultResponse")
                }

            } catch (e : Exception) {
                Log.e(TAG, "getPrediciton:${e.message} ")
            }

        }
    }

    companion object {
        private val TAG = SensorInputViewModel::class.simpleName
    }

}