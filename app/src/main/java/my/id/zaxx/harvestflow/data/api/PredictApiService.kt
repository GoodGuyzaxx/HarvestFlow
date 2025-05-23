package my.id.zaxx.harvestflow.data.api

import com.google.gson.JsonObject
import my.id.zaxx.harvestflow.data.api.response.PredictionResponse
import retrofit2.http.Body

import retrofit2.http.Headers
import retrofit2.http.POST

interface PredictApiService {
    @POST("/predict")
    @Headers("Content-Type: application/json")
    suspend fun getPrediction(@Body predictJSONObject: JsonObject): PredictionResponse
}