package my.id.zaxx.harvestflow.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    val predictURL = "https://terrapin-relaxing-seemingly.ngrok-free.app/"

    @Provides
    @Singleton
    fun providerHttpLoggingInterceptor() = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    @Provides
    @Singleton
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("Normal")
    fun providerRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseURL)
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun providerApiService(@Named("Normal")retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)



    @Provides
    @Singleton
    @Named("retrofitPredict")
    fun providerSecondRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(predictURL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun providerSecondApiService(@Named("retrofitPredict") retrofit: Retrofit): PredictApiService = retrofit.create(
        PredictApiService::class.java)

    @Provides
    @Singleton
    fun providerRepository(apiService: ApiService, predictApiService: PredictApiService) = HarvestFlowRepository(apiService, predictApiService)


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