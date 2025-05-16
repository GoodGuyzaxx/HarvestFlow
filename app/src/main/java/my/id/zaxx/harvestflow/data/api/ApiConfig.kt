package my.id.zaxx.harvestflow.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.id.zaxx.harvestflow.BuildConfig
import my.id.zaxx.harvestflow.data.repository.HarvestFlowRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiConfig {

    val baseURL = "https://api.openweathermap.org/"
    val predictURL = "http://0.0.0.0:5000"

    @Singleton
    @Provides
    fun providerHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }


    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun providerRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseURL)
        .client(okHttpClient)
        .build()


    @Singleton
    @Provides
    fun providerApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    @Named("retrofitPredict")
    fun providerSecondRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(predictURL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providerSecondApiService(@Named("retrofitPredict") retrofit: Retrofit): PredictApiService = retrofit.create(
        PredictApiService::class.java)

    @Singleton
    @Provides
    fun providerRepository(apiService: ApiService) = HarvestFlowRepository(apiService)

}

//fun getApiService(): ApiService {
//    val loggingInterceptor = if (BuildConfig.DEBUG) {
//        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//    } else
//        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
//    val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://api.openweathermap.org/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .build()
//    return retrofit.create(ApiService::class.java)
//}