package com.example.wutmygainz.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.coinbase.com/v2/"

// Use Moshi Builder to create Moshi object w. KotlinJsonAdapterFactory
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

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
    @GET("prices/BTC-USD/spot")
    suspend fun getBTCPrice():
        Response<CryptoObject>

//    @GET("pokemon/{id}")
//    suspend fun getSpritesList(@Path("id") id: Int):
//            Response<Pokemon>
}

// Creates API object using Retrofit to implement API Service
object CoinbaseApi {
    val retrofitService: CoinbaseApiService by lazy {
        retrofit.create(CoinbaseApiService::class.java)
    }
}
