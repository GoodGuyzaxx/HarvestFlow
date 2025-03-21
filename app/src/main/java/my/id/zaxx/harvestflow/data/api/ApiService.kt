package my.id.zaxx.harvestflow.data.api

import my.id.zaxx.harvestflow.BuildConfig
import my.id.zaxx.harvestflow.data.api.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("data/2.5/weather?appid="+BuildConfig.API_KEY)
    suspend fun getWeatherInfo(
        @Query("lat") lat : String,
        @Query("lon") lon : String
    ): WeatherResponse
}