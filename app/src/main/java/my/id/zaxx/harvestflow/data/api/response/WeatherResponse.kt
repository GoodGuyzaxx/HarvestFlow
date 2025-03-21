package my.id.zaxx.harvestflow.data.api.response

import com.google.gson.annotations.SerializedName


data class WeatherResponse(

	@field:SerializedName("visibility")
	val visibility: Int,

	@field:SerializedName("timezone")
	val timezone: Int,

	@field:SerializedName("main")
	val main: Main,

	@field:SerializedName("clouds")
	val clouds: Clouds,

	@field:SerializedName("sys")
	val sys: Sys,

	@field:SerializedName("dt")
	val dt: Int,

	@field:SerializedName("coord")
	val coord: Coord,

	@field:SerializedName("weather")
	val weather: List<WeatherItem>,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("cod")
	val cod: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("base")
	val base: String,

	@field:SerializedName("wind")
	val wind: Wind
)

data class WeatherItem(

	@field:SerializedName("icon")
	val icon: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("main")
	val main: String,

	@field:SerializedName("id")
	val id: Int
)

data class Main(

	@field:SerializedName("temp")
	val temp: Any,

	@field:SerializedName("temp_min")
	val tempMin: Any,

	@field:SerializedName("grnd_level")
	val grndLevel: Int,

	@field:SerializedName("humidity")
	val humidity: Int,

	@field:SerializedName("pressure")
	val pressure: Int,

	@field:SerializedName("sea_level")
	val seaLevel: Int,

	@field:SerializedName("feels_like")
	val feelsLike: Any,

	@field:SerializedName("temp_max")
	val tempMax: Any
)

data class Wind(

	@field:SerializedName("deg")
	val deg: Int,

	@field:SerializedName("speed")
	val speed: Any,

	@field:SerializedName("gust")
	val gust: Any
)

data class Sys(

	@field:SerializedName("country")
	val country: String,

	@field:SerializedName("sunrise")
	val sunrise: Int,

	@field:SerializedName("sunset")
	val sunset: Int
)

data class Clouds(

	@field:SerializedName("all")
	val all: Int
)

data class Coord(

	@field:SerializedName("lon")
	val lon: Any,

	@field:SerializedName("lat")
	val lat: Any
)
