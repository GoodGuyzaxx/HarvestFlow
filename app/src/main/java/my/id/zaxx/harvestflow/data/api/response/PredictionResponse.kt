package my.id.zaxx.harvestflow.data.api.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(

	@field:SerializedName("prediction")
	val prediction: String,

	@field:SerializedName("probabilities")
	val probabilities: Probabilities,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("message")
	val message: String
)

data class Probabilities(

	@field:SerializedName("buruk")
	val buruk: String,

	@field:SerializedName("baik")
	val baik: String
)
