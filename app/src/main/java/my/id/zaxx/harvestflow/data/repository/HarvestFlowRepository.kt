package my.id.zaxx.harvestflow.data.repository

import my.id.zaxx.harvestflow.data.api.ApiService
import my.id.zaxx.harvestflow.data.api.response.WeatherResponse


class HarvestFlowRepository(
    private val apiService : ApiService
){
    suspend fun getWeather(lat: String, lon : String): WeatherResponse {
        return apiService.getWeatherInfo(lat,lon)
    }
}