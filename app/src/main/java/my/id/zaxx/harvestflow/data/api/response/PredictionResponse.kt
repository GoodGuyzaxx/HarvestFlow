package my.id.zaxx.harvestflow.data.api.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(

	@field:SerializedName("prediction")
	val prediction: String,

	@field:SerializedName("probabilities")
	val probabilities: List<Any>,

	@field:SerializedName("status")
	val status: String
)
