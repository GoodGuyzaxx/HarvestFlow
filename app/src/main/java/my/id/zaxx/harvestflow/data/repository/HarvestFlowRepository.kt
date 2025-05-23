package my.id.zaxx.harvestflow.data.repository

import com.google.gson.JsonObject
import my.id.zaxx.harvestflow.data.api.ApiService
import my.id.zaxx.harvestflow.data.api.PredictApiService
import my.id.zaxx.harvestflow.data.api.response.PredictionResponse
import my.id.zaxx.harvestflow.data.api.response.WeatherResponse



class HarvestFlowRepository(
    private val apiService : ApiService,
    private val predictApi : PredictApiService
){
    suspend fun getWeather(lat: String, lon : String): WeatherResponse {
        return apiService.getWeatherInfo(lat,lon)
    }

    suspend fun getPredication(predictJSON: JsonObject): PredictionResponse {
        return predictApi.getPrediction(
            predictJSON
        )
    }
}