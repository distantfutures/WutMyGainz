package com.example.wutmygainz.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.coinbase.com/v2/"

// Fetches JSON response. Create Retrofit Builder using scalars converter factory & give base url of web service.
private val retrofit = Retrofit.Builder()
    // Turns Json response into Kotlin Objects
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

// Implement API service interface that return JSON data as a string (through Scalars)
interface CoinbaseApiService {
    // Gets answers objects
    @GET("prices/{currency_pair}/spot")
    suspend fun getHistoricCoinPrice(@Path("currency_pair") pair: String, @Query("date") type: String):
        Response<DataObject>

    @GET("prices/{currency_pair}/spot")
    suspend fun getCurrentCoinPrice(@Path("currency_pair") pair: String):
        Response<DataObject>
}

// Creates API object using Retrofit to implement API Service
object CoinbaseApi {
    val retrofitService: CoinbaseApiService by lazy {
        retrofit.create(CoinbaseApiService::class.java)
    }
}
