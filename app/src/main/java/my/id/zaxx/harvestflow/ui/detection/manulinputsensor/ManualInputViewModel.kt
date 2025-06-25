package my.id.zaxx.harvestflow.ui.detection.manulinputsensor

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
class ManualInputViewModel @Inject constructor(private val repository: HarvestFlowRepository) : ViewModel() {

    private val _responseValue = MutableLiveData<PredictionResponse>()
    val responseValue : LiveData<PredictionResponse> =_responseValue

    fun getPrediciton(jsonObject: JsonObject) {
        viewModelScope.launch {
            try {
                val response = repository.getPredication(jsonObject)
                _responseValue.postValue(response)
            }catch (e : HttpException) {
                try {
                    val jsonString = e.response()?.errorBody()?.string()
                    if (!jsonString.isNullOrEmpty()){
                        val errorBody = Gson().fromJson(jsonString, PredictionResponse::class.java)
                        val errorMessage = errorBody
                        _responseValue.postValue(errorMessage)
                        Log.e("TAG", "getPrediciton:$errorMessage ")
                    } else {
                        val defaultResponse = PredictionResponse(
                            prediction = "",
                            probabilities = Probabilities("",""),
                            status = "error",
                            message = "HTTP: ${e.code()} + ${e.response()} "
                        )
                        _responseValue.postValue(defaultResponse)
                        Log.d(TAG, "getPrediciton: $defaultResponse")
                    }
                } catch (json : Exception) {
                    val jsonDefaultResponse = PredictionResponse(
                        prediction = "",
                        probabilities = Probabilities("",""),
                        status = "error",
                        message = "Failed ${json.message} "
                    )
                    _responseValue.postValue(jsonDefaultResponse)
                    Log.d(TAG, "getPrediciton: $jsonDefaultResponse")
                }

            } catch (e : Exception) {
                Log.e(TAG, "getPrediciton:${e.message} ")
            }

        }
    }
    
    companion object {
        private val TAG = ManualInputViewModel::class.simpleName
    }

}